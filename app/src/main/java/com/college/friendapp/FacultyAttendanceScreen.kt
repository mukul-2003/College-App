//package com.example.littlelemon
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun FacultyAttendanceScreen(navController: NavController) {
//    val coroutineScope = rememberCoroutineScope()
//    var studentId by remember { mutableStateOf("") }
//    var isPresent by remember { mutableStateOf(true) }
//    var message by remember { mutableStateOf("") }
//
//    // Default to today's date
//    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    var date by remember { mutableStateOf(dateFormat.format(Date())) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Mark Attendance", style = MaterialTheme.typography.titleLarge)
//
//        OutlinedTextField(
//            value = studentId,
//            onValueChange = { studentId = it.trim() },
//            label = { Text("Student UID") },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//        )
//
//        OutlinedTextField(
//            value = date,
//            onValueChange = { date = it.trim() },
//            label = { Text("Date (yyyy-MM-dd)") },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//        )
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Checkbox(
//                checked = isPresent,
//                onCheckedChange = { isPresent = it }
//            )
//            Text(text = "Present")
//        }
//
//        Button(
//            onClick = {
//                if (studentId.isNotBlank() && date.isNotBlank()) {
//                    coroutineScope.launch {
//                        try {
//                            FirestoreService.markAttendance(studentId, date, isPresent)
//                            message = "Attendance marked successfully"
//                        } catch (e: Exception) {
//                            message = "Failed to mark attendance: ${e.message}"
//                        }
//                    }
//                } else {
//                    message = "Please fill all fields"
//                }
//            },
//            modifier = Modifier.padding(top = 12.dp)
//        ) {
//            Text("Mark Attendance")
//        }
//
//        if (message.isNotEmpty()) {
//            Text(
//                text = message,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//    }
//}
