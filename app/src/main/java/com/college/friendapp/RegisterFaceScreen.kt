package com.college.friendapp

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun RegisterFaceScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var facultyList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val snapshot = FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "faculty")
                .get()
                .await()

            facultyList = snapshot.documents.map {
                it.id to (it.getString("name") ?: "Unnamed Faculty")
            }
        } catch (e: Exception) {
            errorMessage = "Error fetching faculty: ${e.message}"
            Log.e("RegisterFaceScreen", "Firestore error", e)
        }
    }

    TopAppBar(
        navController = navController,
        context = context,
        drawerState = rememberDrawerState(DrawerValue.Closed),
        scope = scope,
        timetable = emptyMap(),
        showTabs = false,
        screenTitle = "Register Face"
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(facultyList) { (uid, name) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { navController.navigate("registerFaceCapture/$uid") }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(name, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
