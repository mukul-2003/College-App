package com.example.littlelemon

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (item.title == "Attendance") {
                                    navController.navigate("attendance")
                                }
                                if (item.title == "Time-Table") {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    navController.navigate("timetable/$userId")
                                }
                                if (item.title == "Reset Password") {
                                    navController.navigate("resetPassword")
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
                androidx.compose.material.TopAppBar(
                    backgroundColor = Color(11, 11, 69),
                    contentColor = Color.White,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        Text(
                            text = screenTitle,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(Login.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logout),
                                contentDescription = "logout",
                                modifier = Modifier
                                    .size(24.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (showTabs) {
                        // Time Table Content
                        DaysTabLayout(timetable)
                    } else {
                        // Attendance Content passed from AttendanceNavigation
                        content()
                    }
                }
            }
        )
    }
}
