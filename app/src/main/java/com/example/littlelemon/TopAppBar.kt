package com.example.littlelemon

import DaysTabLayout
import android.content.Context
import android.graphics.fonts.Font
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopAppBar(navController: NavController, context: Context, drawerState: DrawerState, scope: CoroutineScope, timetable: Map<String, List<TimetableEntry>>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight().width(250.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.title)
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
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
                            text = "Time Table",
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    },

                    actions = {
                        IconButton(
                            onClick = { setLoggedInState(context, false)
                                navController.navigate(Login.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        ) {
                            Image(painter = painterResource(id = R.drawable.logout), contentDescription = "logout", modifier = Modifier
                                .size(24.dp)
                                .fillMaxWidth()
                            )
                        }
                    }
                )
            },
            content = { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Add timetable content in the Row
                    Box(modifier = Modifier.weight(1f)) {
                        DaysTabLayout(timetable) // Your timetable UI
                    }
                }
            }
        )
    }
}

//@Composable
//fun TopAppBar(navController: NavController, context: Context, drawerState: DrawerState, scope: CoroutineScope) {
//    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.075f).background(color = Color(11, 11, 69)), verticalAlignment = Alignment.CenterVertically ) {
//        IconButton(onClick = {
//            scope.launch {
//                drawerState.open()}
//        }) {
//            Image(painter = painterResource(id = R.drawable.hamburger), contentDescription = "Menu Icon", modifier = Modifier
//                .size(24.dp)
//                .fillMaxWidth())
//        }
//        Text(text = "Time Table", modifier = Modifier, color = Color.White, fontSize = 20.sp)
//        //Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Little Lemon Logo", modifier = Modifier.fillMaxWidth(0.5f).padding(horizontal = 20.dp))
//        IconButton(onClick = { setLoggedInState(context, false)
//            navController.navigate(Login.route) {
//                popUpTo(navController.graph.startDestinationId) {
//                    inclusive = true
//                }
//            }
//        }) {
//            Image(painter = painterResource(id = R.drawable.logout), contentDescription = "logout", modifier = Modifier
//                .size(24.dp)
//                .fillMaxWidth())
//        }
//    }
//}