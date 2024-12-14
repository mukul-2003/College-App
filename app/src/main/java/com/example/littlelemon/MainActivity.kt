package com.example.littlelemon

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
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

    override fun onBackPressed() {
        val navController = (findViewById<View>(android.R.id.content) as NavHost).navController
        if (navController.currentDestination?.id == navController.graph.startDestinationId) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}

@Composable
fun MyApp(){
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = Login.route){
        composable(Login.route){
            LoginScreen(navController)
        }
        composable(Home.route){
            MyNavigation(navController, context)
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
