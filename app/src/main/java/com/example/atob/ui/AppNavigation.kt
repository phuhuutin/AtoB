package com.example.atob.ui
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atob.BuildConfig
import com.example.atob.R
import com.example.atob.model.ReportDTO
import com.example.atob.ui.screen.HomeScreen
import com.example.atob.ui.screen.InitialSetupScreen
import com.example.atob.ui.screen.LoginScreen
import com.example.atob.ui.screen.PayrollScreen
import com.example.atob.ui.screen.ProfileScreen
import com.example.atob.ui.screen.ReportScreen
import com.example.atob.ui.screen.Screen
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.FindShiftViewModel
import com.example.atob.ui.viewModel.HomeViewModel
import com.example.atob.ui.viewModel.PayrollViewModel
import com.example.atob.ui.viewModel.ReportViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory), modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState() // Collecting the UI state
    val employer = (uiState as? AuthViewUiState.Success)?.userInfo?.employer
    val isAuthenticated = (uiState as? AuthViewUiState.Success)?.userInfo ?: null

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val findShiftViewModel: FindShiftViewModel = viewModel(factory = FindShiftViewModel.Factory)
    val payrollViewModel: PayrollViewModel = viewModel(factory = PayrollViewModel.Factory)
    val reportViewModel: ReportViewModel = viewModel(factory = ReportViewModel.Factory)


    val loginUpdateHeader: suspend (username: String, password: String) -> Unit = { username, password ->

            authViewModel.loginSuspend(username, password) // Call the suspend function
            payrollViewModel.updateNewAuthHeader() // Update PayrollViewModel
            homeViewModel.updateNewAuthHeader() // Update HomeViewModel
            findShiftViewModel.updateNewAuthHeader() // Update FindShiftViewModel
            reportViewModel.updateNewAuthHeader() // Update ReportViewModel

    }




    val updateNewAuthHeader:  () -> Unit = {
        homeViewModel.updateNewAuthHeader() // Update HomeViewModel
        findShiftViewModel.updateNewAuthHeader() // Update FindShiftViewModel
        payrollViewModel.updateNewAuthHeader() // Update PayrollViewModel
        reportViewModel.updateNewAuthHeader() // Update ReportViewModel

    }
    LaunchedEffect(isAuthenticated) {
        Log.d("AppNavigation", "isAuthenticated: ${BuildConfig.LocalHost}")

         if(isAuthenticated != null){
            homeViewModel.updateEmployer(isAuthenticated.employer)
            findShiftViewModel.updateEmployer(isAuthenticated.employer)
            payrollViewModel.updatePayRate(isAuthenticated.payRate)
        }else{
             // loginUpdateHeader("tinmene","1234567")
        }

    }


    Scaffold(
        bottomBar = {
            if (isAuthenticated != null) {
                BottomNavigationBar(navController) // Make sure this uses NavigationBar/NavigationBarItem
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated != null) Screen.Home.route else Screen.Login.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) { LoginScreen(navController, authViewModel, onLoginClick = loginUpdateHeader ) }
            composable(Screen.Home.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    modifier = modifier,
                    authViewModel = authViewModel,
                    findShiftViewModel = findShiftViewModel,
                    reportViewModel = reportViewModel
                )
            }
            composable(Screen.Pay.route) { PayrollScreen(payrollViewModel = payrollViewModel) }
            composable(Screen.Profile.route) { ProfileScreen(navController= navController, authViewModel = authViewModel, updateNewCredetial = updateNewAuthHeader) }
            composable(Screen.Report.route) { ReportScreen(reportViewModel = reportViewModel, authViewModel = authViewModel) }
            composable(Screen.InitialSetup.route) {
                InitialSetupScreen(
                    authViewModel = authViewModel,
                    navController,
                    onSetupComplete = { setupData ->
                        // Handle the setup data (e.g., save to the database)
                        loginUpdateHeader(setupData.username, setupData.password)
                        navController.popBackStack() // Navigate back to the previous screen
                    },
                    onAddressConfirm = {}
                )
            }
        }
    }
}