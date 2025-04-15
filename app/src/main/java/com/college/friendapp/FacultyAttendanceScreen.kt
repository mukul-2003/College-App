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

    var attendanceData by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun fetchAttendance() {
        try {
            isLoading = true
            val doc = FirebaseFirestore.getInstance().collection("attendance")
                .document(uid).get().await()
            val data = doc.get("attendance") as? Map<String, Boolean>
            attendanceData = data ?: emptyMap()
            errorMessage = ""
        } catch (e: Exception) {
            errorMessage = "Failed to load attendance"
        } finally {
            isLoading = false
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        scope.launch { fetchAttendance() }
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
                val total = attendanceData.size
                val present = attendanceData.values.count { it }
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            scope.launch { fetchAttendance() }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.refresh), // uses your refresh.png
                            contentDescription = "Refresh Attendance",
                            tint = Color(11, 11, 69),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendanceData.entries.toList()) { entry ->
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
                                    color = if (entry.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
