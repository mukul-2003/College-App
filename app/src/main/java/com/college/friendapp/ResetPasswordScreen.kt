package com.college.friendapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ResetPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    var message by remember { mutableStateOf("") }

    TopAppBar(
        navController = navController,
        context = context,
        drawerState = drawerState,
        scope = coroutineScope,
        timetable = emptyMap(),
        showTabs = false,
        screenTitle = "Reset Password"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = TextFieldValue(currentUserEmail),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (currentUserEmail.isNotEmpty()) {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(currentUserEmail)
                            .addOnCompleteListener { task ->
                                message = if (task.isSuccessful) {
                                    "Reset link sent to $currentUserEmail"
                                } else {
                                    task.exception?.message ?: "Error sending reset link"
                                }
                            }
                    } else {
                        message = "No user logged in"
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(11, 11, 69)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Send Reset Link", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (message.isNotEmpty()) {
                Text(text = message, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}
