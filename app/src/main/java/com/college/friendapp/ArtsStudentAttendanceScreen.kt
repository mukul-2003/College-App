package com.college.friendapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ArtsStudentAttendanceScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var subjectAttendance by remember { mutableStateOf<Map<String, Map<String, Boolean>>>(emptyMap()) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var transitionLoading by remember { mutableStateOf(false) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 🔄 Load attendance data
    suspend fun fetchAttendance() {
        isLoading = true
        errorMessage = ""
        try {
            val doc = FirebaseFirestore.getInstance().collection("attendance")
                .document(uid).get().await()
            val rawData = doc.get("attendance") as? Map<String, Map<String, Boolean>>
            subjectAttendance = rawData ?: emptyMap()
        } catch (e: Exception) {
            errorMessage = "Failed to load attendance: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // 🔁 Reload on first load
    LaunchedEffect(Unit) {
        scope.launch {
            fetchAttendance()
        }
    }

    // 🔙 Handle back press to go back to subject list
    if (selectedSubject != null) {
        BackHandler {
            scope.launch {
                isLoading = true
                delay(100) // Optional transition delay
                selectedSubject = null
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
        screenTitle = "Your Attendance"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> LoadingScreen(message = "")
                errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                selectedSubject == null -> {
                    if (subjectAttendance.isEmpty()) {
                        Text("No attendance records found.", color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(subjectAttendance.keys.toList()) { subject ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            scope.launch {
                                                isLoading = true
                                                delay(100) // Optional delay to simulate loading
                                                selectedSubject = subject
                                                isLoading = false
                                            }
                                        },
                                            shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(subject, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    val data = subjectAttendance[selectedSubject] ?: emptyMap()
                    val total = data.size
                    val present = data.values.count { it }
                    val percentage = if (total > 0) (present * 100) / total else 0

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Percentage: $percentage%",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = {
                                scope.launch { fetchAttendance() }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.refresh),
                                contentDescription = "Refresh Attendance",
                                tint = Color(11, 11, 69),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(data.entries.sortedBy { it.key }) { entry ->
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
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = entry.key)
                                    Text(
                                        text = if (entry.value) "Present" else "Absent",
                                        color = if (entry.value) Color.Green else Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
