package com.college.friendapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FacultyDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var facultyClass by remember { mutableStateOf("") }
    var stream by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            try {
                val doc = FirebaseFirestore.getInstance().collection("users")
                    .document(uid).get().await()
                facultyClass = doc.getString("class") ?: ""
                stream = doc.getString("stream") ?: ""
            } catch (e: Exception) {
                errorMessage = "Failed to load data"
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
        screenTitle = "Faculty Dashboard"
    ) {
        if (isLoading) {
            LoadingScreen(message = "")
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, modifier = Modifier.padding(16.dp))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val userDoc = FirebaseFirestore.getInstance().collection("users")
                                    .document(uid).get().await()
                                val streamVal = userDoc.getString("stream") ?: ""

                                if (streamVal.lowercase() == "arts") {
                                    navController.navigate("artsScheduledClassList")
                                } else {
                                    navController.navigate("markAttendance/$facultyClass")
                                }
                            }
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Mark Attendance")
                        if (stream.lowercase() != "arts") {
                            Text(text = "Class: $facultyClass")
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("facultyAttendance")
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "My Attendance")
                        Text(text = "View your own attendance")
                    }
                }
            }
        }
    }
}
