package com.college.friendapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class StudentAttendance(
    val uid: String,
    val name: String,
    var isPresent: Boolean
)

@Composable
fun MarkAttendanceScreen(navController: NavController, className: String) {
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

    // 🔥 Fetch student list
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("role", "student")
                    .whereEqualTo("class", className) // Ensure this matches Firestore field
                    .get().await()

                val studentList = snapshot.documents.map { doc ->
                    StudentAttendance(
                        uid = doc.id,
                        name = doc.getString("name") ?: "",
                        isPresent = true
                    )
                }
                students = studentList
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (message.isNotEmpty()) {
                Text(text = message, color = MaterialTheme.colorScheme.error)
            } else if (students.isEmpty()) {
                Text(text = "No students found in class: $className")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
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
                                        val updatedList = students.map {
                                            if (it.uid == student.uid) it.copy(isPresent = checked) else it
                                        }
                                        students = updatedList
                                    }
                                )

                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                students.forEach { student ->
                                    val ref = FirebaseFirestore.getInstance()
                                        .collection("attendance").document(student.uid)
                                    ref.update(
                                        "attendance.$todayDate",
                                        student.isPresent
                                    ).await()
                                }
                                message = "Attendance marked successfully"
                            } catch (e: Exception) {
                                message = "Error marking attendance: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Submit Attendance")
                }

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
