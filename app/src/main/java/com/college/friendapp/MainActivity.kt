package com.college.friendapp

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun NetworkErrorScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No Internet Connection")
    }
}

@Composable
fun MyApp(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val networkState = remember { NetworkState(context) }
    DisposableEffect(Unit) {
        networkState.startMonitoring()
        onDispose { networkState.stopMonitoring() }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (networkState.isConnected.value) {
            NavHost(navController = navController, startDestination = Login.route) {
                composable(Login.route) {
                    LoginScreen(navController)
                }
                composable(
                    route = "timetable/{username}",
                    arguments = listOf(navArgument("username") { type = NavType.StringType })
                ) { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    MyNavigation(navController, username)
                }
                composable("attendance") {
                    AttendanceNavigation(navController)
                }
                composable("resetPassword") {
                    ResetPasswordScreen(navController)
                }
                composable("facultyDashboard") {
                    FacultyDashboardScreen(navController)
                }
                composable("studentAttendance") {
                    StudentAttendanceScreen(navController)
                }
                composable("facultyAttendance") {
                    FacultyAttendanceScreen(navController)
                }
                composable(
                    route = "markAttendance/{className}",
                    arguments = listOf(navArgument("className") { type = NavType.StringType })
                ) { backStackEntry ->
                    val className = backStackEntry.arguments?.getString("className") ?: ""
                    MarkAttendanceScreen(navController, className)
                }
            }
        } else {
            NetworkErrorScreen(onRetry = {
                networkState.startMonitoring()
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreviewLight(){
    MyApp()
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyAppPreviewDark(){
    MyApp()
}
