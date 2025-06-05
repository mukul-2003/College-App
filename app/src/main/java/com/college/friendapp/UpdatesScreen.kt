package com.college.friendapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UpdatesScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var updates by remember {
        mutableStateOf<List<com.google.firebase.firestore.DocumentSnapshot>>(emptyList())
    }
    var isLoading by remember { mutableStateOf(true) }
    val userRole = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
            val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
            val userRoleValue = userDoc.getString("role") ?: "student"
            val userName = userDoc.getString("name") ?: ""
            userRole.value = userRoleValue

            val snapshot = FirebaseFirestore.getInstance()
                .collection("updates")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            updates = snapshot.documents.filter { doc ->
                val targetType = doc.getString("targetType") ?: ""
                val postedBy = doc.getString("postedBy") ?: ""
                val postedName = doc.getString("postedName") ?: ""

                when (userRoleValue) {
                    "admin" -> true
                    "faculty" -> targetType in listOf("faculty", "all") ||
                            (postedBy == "faculty" && postedName.equals(userName, ignoreCase = true))
                    "student" -> targetType in listOf("student", "all")
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e("UpdatesScreen", "Failed to load updates", e)
        } finally {
            isLoading = false
        }
    }

    TopAppBar(
        navController = navController,
        context = context,
        drawerState = drawerState,
        scope = scope,
        timetable = emptyMap(),
        showTabs = false,
        screenTitle = "Updates"
    ) {
        if (isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (updates.isEmpty()) {
                    item {
                        Text("No updates found.", color = Color.Gray)
                    }
                } else {
                    itemsIndexed(updates) { _, doc ->
                        val title = doc.getString("title") ?: "No Title"
                        val message = doc.getString("message") ?: "No message"
                        val postedBy = doc.getString("postedName") ?: "Unknown"
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()
                        val timeAgo = timestamp?.let { getTimeAgo(it) } ?: ""

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                        color = Color(11, 11, 69)
                                    )
                                    Text(
                                        text = timeAgo,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = message)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Posted by: $postedBy", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getTimeAgo(timestamp: Date): String {
    val now = Date().time
    val diff = now - timestamp.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(timestamp)
    }
}
