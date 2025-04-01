package com.college.friendapp

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TimetableEntry(
    val time: String = "",
    val subject: String = "",
    val location: String = ""
)

@Composable
fun MyNavigation(navController: NavController, userId: String) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var timetable by remember { mutableStateOf<Map<String, List<TimetableEntry>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }

    // Handle Back Press → Show Exit Confirmation
    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit App") },
            text = { Text("Do you want to exit the app?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Fetch Timetable
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                timetable = fetchUserTimetable(userId)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error fetching timetable"
            } finally {
                isLoading = false
            }
        }
    }

    // UI Content
    when {
        isLoading -> {
            LoadingScreen(message = "Loading...")
        }
        errorMessage.isNotEmpty() -> {
            Text(text = "Error: $errorMessage")
        }
        else -> {
            Column {
                TopAppBar(
                    navController = navController,
                    context = context,
                    drawerState = drawerState,
                    scope = coroutineScope,
                    timetable = timetable
                ) {
                    // Content can go here if you want
                }
            }
        }
    }
}

suspend fun fetchUserTimetable(userId: String): Map<String, List<TimetableEntry>> {
    val db = FirebaseFirestore.getInstance()

    val userDoc = db.collection("users").document(userId).get().await()
    val timetableId = userDoc.getString("timetable") ?: throw Exception("No timetableId found")

    val timetableDoc = db.collection("timetables").document(timetableId).get().await()

    val timetableMap = mutableMapOf<String, List<TimetableEntry>>()

    for (day in timetableDoc.data?.keys ?: emptySet()) {
        val rawList = timetableDoc.get(day) as? List<Map<String, Any>> ?: continue
        val entries = rawList.map {
            TimetableEntry(
                subject = it["Subject"]?.toString() ?: "",
                time = it["Time"]?.toString() ?: "",
                location = it["Location"]?.toString() ?: ""
            )
        }
        timetableMap[day] = entries
    }

    return timetableMap
}
