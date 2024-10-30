package com.example.atob.ui
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atob.ui.screen.HomeScreen
import com.example.atob.ui.screen.LoginScreen
import com.example.atob.ui.screen.ProfileScreen
import com.example.atob.ui.screen.Screen
import com.example.atob.ui.screen.SettingsScreen
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.HomeViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState() // Collecting the UI state
    val isAuthenticated = uiState.isAuthenticated
    val homeViewModel: HomeViewModel =  viewModel(factory = HomeViewModel.Factory)
        //al authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                BottomNavigationBar(navController) // Make sure this uses NavigationBar/NavigationBarItem
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) { LoginScreen(navController, authViewModel) }
            composable(Screen.Home.route) { HomeScreen(homeViewModel) }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}