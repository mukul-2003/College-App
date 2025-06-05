package com.college.friendapp

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SendUpdateScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var targetOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTarget by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                userRole = userDoc.getString("role") ?: ""

                if (userRole == "admin") {
                    targetOptions = listOf("All", "Student", "Faculty")
                    selectedTarget = "all"
                } else if (userRole == "faculty") {
                    val classList = userDoc.get("classes") as? List<*>
                    if (!classList.isNullOrEmpty()) {
                        targetOptions = classList.filterIsInstance<String>()
                        selectedTarget = targetOptions.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading user info", Toast.LENGTH_SHORT).show()
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
        screenTitle = "Send Update"
    ) {
        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = if (userRole == "admin") "Send To:" else "Class:")
                DropdownMenuBox(
                    options = targetOptions,
                    selected = selectedTarget ?: "",
                    onSelectedChange = { selectedTarget = it }
                )

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // ✅ ADD THIS LINE
                                val userDoc = FirebaseFirestore.getInstance().collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser!!.uid).get().await()

                                val postData = mapOf(
                                    "title" to title,
                                    "message" to message,
                                    "target" to selectedTarget,
                                    "targetType" to if (userRole == "admin") selectedTarget else "student",
                                    "postedBy" to userRole,
                                    "postedName" to userDoc.getString("name"), // ✅ USE NAME FIELD
                                    "timestamp" to com.google.firebase.Timestamp.now()
                                )

                                FirebaseFirestore.getInstance().collection("updates")
                                    .add(postData)
                                    .await()

                                Toast.makeText(context, "Update sent!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(11, 11, 69))
                ) {
                    Text("Send", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Send To") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
