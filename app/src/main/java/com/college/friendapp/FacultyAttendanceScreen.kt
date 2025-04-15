package com.college.friendapp

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FacultyAttendanceScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Update: store String instead of Boolean
    var attendanceData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val doc = FirebaseFirestore.getInstance().collection("attendance")
                    .document(uid).get().await()
                val data = doc.get("attendance") as? Map<String, String>
                attendanceData = data ?: emptyMap()
            } catch (e: Exception) {
                errorMessage = "Failed to load attendance"
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
        screenTitle = "My Attendance"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            } else {
                val present = attendanceData.values.count { it == "Present" }
                val halfDay = attendanceData.values.count { it == "Half Day" }
                val absent = attendanceData.values.count { it == "Absent" }

                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "P: ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "$present",
                            color = Color.Green, // Green
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(text = "  H: ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "$halfDay",
                            color = Color.Yellow, // Yellow
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(text = "  A: ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "$absent",
                            color = Color.Red, // Red
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(
                            onClick = {
                                isLoading = true
                                errorMessage = ""
                                scope.launch {
                                    try {
                                        val doc = FirebaseFirestore.getInstance().collection("attendance")
                                            .document(uid).get().await()
                                        val data = doc.get("attendance") as? Map<String, String>
                                        attendanceData = data ?: emptyMap()
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to refresh attendance"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.refresh), // uses your refresh.png
                                contentDescription = "Refresh Attendance",
                                tint = Color(11, 11, 69),
                                modifier = Modifier.size(20.dp).offset(y = 2.dp)
                            )
                        }
                    }


                }


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendanceData.entries.toList()) { entry ->
                        val status = entry.value
                        val statusColor = when (status) {
                            "Present" -> Color.Green
                            "Half Day" -> Color.Yellow
                            "Absent" -> Color.Red
                            else -> Color.Gray
                        }

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
                                Text(text = status, color = statusColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
