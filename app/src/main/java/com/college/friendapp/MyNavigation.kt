package com.college.friendapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
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

    if (isLoading) {
        LoadingScreen(message = "Loading..")
    } else if (errorMessage.isNotEmpty()) {
        androidx.compose.material.Text(text = "Error: $errorMessage")
    } else {
        Column {
            TopAppBar(navController, context, drawerState, coroutineScope, timetable){}
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
                time = it["Time"]?.toString() ?: ""
            )
        }
        timetableMap[day] = entries
    }

    return timetableMap
}
