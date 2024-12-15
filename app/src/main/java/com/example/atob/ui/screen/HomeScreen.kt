package com.example.atob.ui.screen

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atob.model.ReportDTO
import com.example.atob.ui.state.AuthViewUiState

import com.example.atob.ui.viewModel.AddressViewModel
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.HomeViewModel

import java.time.LocalDate

import com.example.atob.ui.viewModel.FindShiftViewModel
import com.example.atob.ui.viewModel.ReportViewModel


enum class MarkColor(
    val backgroundColor: @Composable () -> Color,
    val textColor: @Composable () -> Color
) {
    PRIMARY(
        backgroundColor = { MaterialTheme.colorScheme.primary },
        textColor = { MaterialTheme.colorScheme.onPrimary }
    ),
    SECONDARY(
        backgroundColor = { MaterialTheme.colorScheme.onTertiaryContainer },
        textColor = { MaterialTheme.colorScheme.onSecondary }
    ),
    ERROR(
        backgroundColor = { MaterialTheme.colorScheme.error },
        textColor = { MaterialTheme.colorScheme.onError }
    ),
    NONE(
        backgroundColor = { MaterialTheme.colorScheme.surface },
        textColor = { MaterialTheme.colorScheme.onSurface }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    findShiftViewModel: FindShiftViewModel = viewModel(factory = FindShiftViewModel.Factory),
    reportViewModel: ReportViewModel = viewModel(factory = ReportViewModel.Factory)
 ) {
    var selectedTabIndex by rememberSaveable  { mutableIntStateOf(0) }
    var selectedDate by rememberSaveable   { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val currentUser by remember { mutableStateOf((authViewModel.uiState.value as? AuthViewUiState.Success)?.userInfo) }
    val onDateSelected: (LocalDate) -> Unit = {
        selectedDate = it
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadShifts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home", color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val tabTitles = listOf("My Schedule", "Find Shift", "Attendance")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        text = { Text(title, color = MaterialTheme.colorScheme.surface) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> MyScheduleScreen(addressViewModel  = viewModel(factory = AddressViewModel.Factory), homeViewModel, authViewModel = authViewModel, modifier = modifier, selectedDate = selectedDate, onDateSelected = onDateSelected, reportViewModel = reportViewModel)
                1 -> FindShiftScreen(findShiftViewModel = findShiftViewModel,
                    modifier = modifier.background(MaterialTheme.colorScheme.onPrimaryContainer),
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
                2 -> AttendancePointsScreen(attendancePoints = currentUser?.attendancePoints!!)
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MyHomeScreenPreview() {
//    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
//    val navController = rememberNavController()
//    val uiState by authViewModel.uiState.collectAsState() // Collecting the UI state
//    authViewModel.login("tinmene","123456")
//    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
//    AtoBTheme {
//        (uiState as AuthViewUiState.Success).userInfo?.let { HomeScreen(homeViewModel = homeViewModel) }
//    }
//
//}


