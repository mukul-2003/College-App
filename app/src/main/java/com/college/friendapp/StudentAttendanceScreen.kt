//package com.example.littlelemon
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import kotlinx.coroutines.launch
//
//@Composable
//fun StudentAttendanceScreen(navController: NavController, userId: String) {
//    val coroutineScope = rememberCoroutineScope()
//    var attendanceData by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var errorMessage by remember { mutableStateOf("") }
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            try {
//                attendanceData = FirestoreService.getAttendanceForStudent(userId)
//            } catch (e: Exception) {
//                errorMessage = e.message ?: "Error fetching attendance"
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Your Attendance") })
//        }
//    ) { padding ->
//        Box(modifier = Modifier
//            .fillMaxSize()
//            .padding(padding)) {
//
//            when {
//                isLoading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//                errorMessage.isNotEmpty() -> {
//                    Text(
//                        text = errorMessage,
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                else -> {
//                    val totalClasses = attendanceData.size
//                    val presentClasses = attendanceData.values.count { it }
//                    val attendancePercentage =
//                        if (totalClasses > 0) (presentClasses * 100) / totalClasses else 0
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp)
//                    ) {
//                        Text(
//                            text = "Attendance: $attendancePercentage%",
//                            style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//
//                        LazyColumn {
//                            items(attendanceData.entries.toList()) { entry ->
//                                Card(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 4.dp)
//                                ) {
//                                    Row(
//                                        modifier = Modifier
//                                            .padding(16.dp)
//                                            .fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.SpaceBetween
//                                    ) {
//                                        Text(text = entry.key) // Date
//                                        Text(
//                                            text = if (entry.value) "Present" else "Absent",
//                                            color = if (entry.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
