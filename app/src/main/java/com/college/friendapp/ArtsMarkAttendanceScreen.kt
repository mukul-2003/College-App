package com.college.friendapp

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArtsMarkAttendanceScreen(
    navController: NavController,
    className: String,
    subjectName: String
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf<List<StudentAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }

    val todayDate = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date())
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("role", "student")
                    .whereEqualTo("class", className)
                    .get().await()

                students = snapshot.documents.map {
                    StudentAttendance(
                        uid = it.id,
                        name = it.getString("name") ?: "Unnamed",
                        isPresent = true
                    )
                }
            } catch (e: Exception) {
                message = "Failed to load students: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    TopAppBar(
        navController = navController,
        context = context,
        drawerState = drawerState,
        scope = scope,
        timetable = emptyMap(),
        showTabs = false,
        screenTitle = "Mark Attendance"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                isLoading -> LoadingScreen()
                message.isNotEmpty() -> Text(message, color = MaterialTheme.colorScheme.error)
                else -> {
                    if (students.isEmpty()) {
                        Text(
                            text = "No student enrolled yet.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Class: $className", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(students) { student ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 0.5.dp,
                                            color = Color(11, 69, 69),
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    shape = RoundedCornerShape(6.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = student.name)
                                        Switch(
                                            checked = student.isPresent,
                                            onCheckedChange = { checked ->
                                                students = students.map {
                                                    if (it.uid == student.uid) it.copy(isPresent = checked) else it
                                                }
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(11, 11, 69),
                                                uncheckedThumbColor = Color.White,
                                                uncheckedTrackColor = Color(0xFFCCCCCC)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val db = FirebaseFirestore.getInstance()
                                        students.forEach { student ->
                                            val ref = db.collection("attendance").document(student.uid)
                                            val updateMap = mapOf(
                                                "attendance" to mapOf(
                                                    subjectName to mapOf(
                                                        todayDate to student.isPresent
                                                    )
                                                )
                                            )
                                            ref.set(updateMap, SetOptions.merge()).await()
                                        }
                                        Toast.makeText(context, "Attendance marked!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(11, 11, 69))
                        ) {
                            Text("Submit Attendance", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
