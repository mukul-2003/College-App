package com.example.littlelemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class User(val username: String, val password: String)

private const val PREF_NAME = "com.example.littlelemon.prefs"
private const val KEY_IS_LOGGED_IN = "isLoggedIn"


fun setLoggedInState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    editor.apply()
}

fun loadUsersFromAssets(context: Context): List<User> {
    val users = mutableListOf<User>()
    try {
        val inputStream = context.assets.open("login_credentials.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val jsonArray: JSONArray = jsonObject.getJSONArray("users")

        for (i in 0 until jsonArray.length()) {
            val userObject = jsonArray.getJSONObject(i)
            val username = userObject.getString("username")
            val password = userObject.getString("password")
            users.add(User(username, password))
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return users
}

fun performLogin(context: Context, username: String, password: String): Boolean {
    val users = loadUsersFromAssets(context)
    val user = users.find { it.username == username && it.password == password }
    if (user != null) {
        setLoggedInState(context, true)
        return true
    }
    return false
}

@Composable
fun LoginScreen(navController: NavHostController){
    val context = LocalContext.current
    var showError by remember {
        mutableStateOf(false)
    }

//    val interactionSource = remember { MutableInteractionSource() }
//    var isUnderlined by remember { mutableStateOf(false) }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordFocusRequester = remember { FocusRequester() }
    val backgroundColor = Color(230, 230, 230)
    val focusManager = LocalFocusManager.current
    Column(
        Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.collegelogo),
            contentDescription = "Logo Image",
            modifier = Modifier.padding(10.dp).size(150.dp)
        )
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            placeholder = { Text(text = "Username") },

            modifier = Modifier.padding(horizontal = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            )
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(10.dp).focusRequester(passwordFocusRequester),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (performLogin(context, username.value, password.value)) {
                        navController.navigate(Home.route)
                    } else {
                        showError = true
                    }
                }
            )
        )

//        Text(
//            text = "Forgot Password?",
//            color = Color.Blue,
//            style = androidx.compose.ui.text.TextStyle(
//                textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
//            ),
//            modifier = Modifier
//                .padding(end = 20.dp, bottom = 8.dp)
//                .align(Alignment.End)
//                .clickable(
//                    indication = null, // Remove ripple effect
//                    interactionSource = interactionSource // Disable interaction highlights
//                ) {
//                    isUnderlined = true
//                    // Handle Forgot Password Click
//                    // Example: navController.navigate("forgot_password_route")
//                }
//        )

        if (showError) {
            Text(
                text = "Invalid credentials. Please try again.",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

//        if (isUnderlined) {
//            LaunchedEffect(isUnderlined) {
//                delay(200) // 200ms delay before removing the underline
//                isUnderlined = false
//            }
//        }

        Button(
            onClick = { if (performLogin(context, username.value, password.value)) {
                    navController.navigate(Home.route)
                } else {
                    showError = true
                }
            },
            modifier = Modifier.padding(10.dp).width(150.dp),
            colors = ButtonDefaults.buttonColors(
                Color(11, 11, 69),
            ),
            elevation = null
        ) {
            Text(
                text = "Login",
                color = Color.White
            )
        }
    }
}