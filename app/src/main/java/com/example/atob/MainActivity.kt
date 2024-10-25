package com.example.atob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.atob.ui.AppNavigation
import com.example.atob.ui.screen.LoginScreen
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.compose.AtoBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtoBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
                    // create navigation controller
                    val navController = rememberNavController()
                   // LoginScreen(navController = navController ,authViewModel = authViewModel)
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AtoBTheme {
        val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
        val navController = rememberNavController()

        LoginScreen(navController = navController ,authViewModel = authViewModel)
    }
}

