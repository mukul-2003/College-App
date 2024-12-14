package com.example.littlelemon

import android.content.Context
import android.graphics.fonts.Font
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopAppBar(navController: NavController, context: Context, drawerState: DrawerState, scope: CoroutineScope) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.075f).background(color = Color(11, 11, 69)), verticalAlignment = Alignment.CenterVertically ) {
        IconButton(onClick = {
                scope.launch {
                drawerState.open()}
        }) {
            Image(painter = painterResource(id = R.drawable.hamburger), contentDescription = "Menu Icon", modifier = Modifier
                .size(24.dp)
                .fillMaxWidth())
        }
        Text(text = "Time Table", modifier = Modifier, color = Color.White, fontSize = 20.sp)
        //Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Little Lemon Logo", modifier = Modifier.fillMaxWidth(0.5f).padding(horizontal = 20.dp))
        IconButton(onClick = { setLoggedInState(context, false)
            navController.navigate(Login.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }) {
            Image(painter = painterResource(id = R.drawable.logout), contentDescription = "logout", modifier = Modifier
                .size(24.dp)
                .fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview(){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current
    TopAppBar(drawerState = drawerState, scope = coroutineScope, navController = navController, context = context)
}
