package com.example.littlelemon

import DaysTabLayout
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyNavigation(navController: NavController, context: Context){
    Column {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        TopAppBar(navController, context, drawerState, coroutineScope)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(11,11,69))
        )
        DaysTabLayout()
    }
}