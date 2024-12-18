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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class TimetableEntry(
    val time: String,
    val subject: String,
    val location: String
)

@Composable
fun MyNavigation(navController: NavController, username: String){
    val context = LocalContext.current
    val userTimetable = loadUserTimetable(context, username)
    Column {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        TopAppBar(navController, context, drawerState, coroutineScope, timetable = userTimetable)
    }
}

fun loadUserTimetable(context: Context, username: String): Map<String, List<TimetableEntry>> {
    return try {
        val timetableJson = context.assets.open("Timetable.json").bufferedReader().use { it.readText() }
        val timetableMapType = object : TypeToken<Map<String, Map<String, List<TimetableEntry>>>>() {}.type
        val timetableData: Map<String, Map<String, List<TimetableEntry>>> = Gson().fromJson(timetableJson, timetableMapType)

        // Get the timetable for the specified user
        timetableData[username]?.mapKeys { it.key } ?: emptyMap() // Ensure keys are uppercase
    } catch (e: Exception) {
        e.printStackTrace()
        emptyMap()
    }
}

