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
import java.text.SimpleDateFormat
import java.util.*

data class ArtsPeriod(
    val className: String,
    val subject: String,
    val period: String
)

@Composable
fun ArtsScheduledClassListScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var periods by remember { mutableStateOf<List<ArtsPeriod>>(emptyList()) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val today = remember {
        SimpleDateFormat("EEE", Locale.ENGLISH).format(Date()).uppercase()
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                val timetableId = userDoc.getString("timetable") ?: ""

                val timetableDoc = FirebaseFirestore.getInstance().collection("timetables")
                    .document(timetableId).get().await()

                val todayList = timetableDoc.get(today) as? List<Map<String, String>> ?: emptyList()

                periods = todayList.map {
                    ArtsPeriod(
                        className = it["Class"] ?: "Unknown",
                        subject = it["Subject"] ?: "Unknown",
                        period = it["Period"] ?: "Unknown"
                    )
                }

            } catch (e: Exception) {
                error = "Failed to load: ${e.message}"
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
        screenTitle = "Scheduled Classes"
    ) {
        if (isLoading) {
            LoadingScreen()
        } else if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                periods.forEach { period ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable {
                                navController.navigate("artsMarkAttendance/${period.className}_${period.subject}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Class: ${period.className}")
                            Text(
                                text = "Period: " + runCatching { toRomanNumeral(period.period.toInt()) }.getOrDefault(period.period)
                            )
                        }
                    }
                }

                if (periods.isEmpty()) {
                    Text("No scheduled classes today.")
                }
            }
        }
    }
}
