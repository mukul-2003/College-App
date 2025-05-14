package com.college.friendapp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TopAppBar(
    navController: NavController,
    context: Context,
    drawerState: DrawerState,
    scope: CoroutineScope,
    timetable: Map<String, List<TimetableEntry>>,
    showTabs: Boolean = true,
    screenTitle: String = "Time Table",
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight().width(250.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                val auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid ?: ""
                var userRole by remember { mutableStateOf("") }
                val navItems = remember { mutableStateListOf<HamburgerNavigation>() }

                LaunchedEffect(uid) {
                    try {
                        val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                        userRole = userDoc.getString("role") ?: ""

                        val items = when (userRole) {
                            "faculty" -> facultyItems
                            "admin" -> adminItems
                            "student" -> getStudentNavItems(uid)
                            else -> emptyList()
                        }

                        navItems.clear()
                        navItems.addAll(items)
                    } catch (e: Exception) { }
                }

                navItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (item.route.contains("timetable")) {
                                    val userId = auth.currentUser?.uid ?: ""
                                    navController.navigate("timetable/$userId")
                                } else {
                                    navController.navigate(item.route)
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(11, 11, 69)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = screenTitle,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (showTabs) {
                        DaysTabLayout(timetable)
                    } else {
                        content()
                    }
                }
            }
        )
    }
}

suspend fun getStudentNavItems(uid: String): List<HamburgerNavigation> {
    val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
    val stream = userDoc.getString("stream") ?: "COMMERCE"

    return if (stream == "ARTS") {
        studentItems.map {
            if (it.route == "studentAttendance")
                it.copy(route = "artsStudentAttendance")
            else it
        }
    } else {
        studentItems
    }
}
