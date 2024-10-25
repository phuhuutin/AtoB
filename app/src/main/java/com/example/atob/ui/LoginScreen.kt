package com.example.atob.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff


import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atob.model.User
import com.example.atob.ui.viewModel.LoginViewModel
import com.example.compose.AtoBTheme
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()  // Create a coroutine scope for the composable
    var loggedInUser by remember { mutableStateOf<User?>(null) }  // State to store the returned User
    var loginMessage by remember { mutableStateOf("") }  // State to display login messages
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App name title
        Text(
            text = "AtoB",
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username Input Field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
                coroutineScope.launch {
                        loginMessage = "";
                    try {
                        val user = loginViewModel.login(username, password)  // Call login
                        if (user != null) {
                            loggedInUser = user  // Store user if login is successful
                        }
                    } catch (e: Exception) {
                        // Handle the 403 Forbidden case
                        if (e is HttpException && e.code() == 403) {
                            // Display a message or perform an action on 403 Forbidden response
                            // Example: Show a Toast or set an error message state
                            println("Login failed: 403 Forbidden")
                            loginMessage = "Login failed: 403 Forbidden"
                        } else {
                            // Handle other types of errors
                            println("Login failed: ${e.message}")
                            loginMessage = "Login failed: 403 Forbidden"

                        }
                    }
                }
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Display logged-in user details if available
        loggedInUser?.let { user ->
            Text("Logged in as: ${user.username}")
            Text("Email: ${user.email}")
            Text("Role: ${user.role}")
        }

        // Display login message
        if(!loginMessage.isEmpty()){
            Text(loginMessage, color = Color.Red)
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewLoginScreen() {
//    AtoBTheme {
//        LoginScreen(onLoginClick = { _, _ -> /* handle login */ })
//    }
//}