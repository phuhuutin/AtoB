package com.example.atob.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.atob.model.Shift
import com.example.atob.ui.viewModel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Define the tabs
            val tabTitles = listOf("My Schedule", "Find Shift")

            // TabRow for switching between "My Schedule" and "Find Shift"
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Display content based on selected tab
            when (selectedTabIndex) {
                0 -> MyScheduleScreen(viewModel)
                1 -> FindShiftScreen(viewModel)
            }
        }
    }
}



@Composable
fun FindShiftScreen(viewModel: HomeViewModel) {
    // Display shifts that user can pick

}

@Composable
fun MyScheduleScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Using a Box to align content properly
    Box(modifier = Modifier.fillMaxSize()) {
        // Show loading indicator if data is loading
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            uiState.errorMessage?.let { errorMessage ->
                Text("Error: $errorMessage", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }

            // Displaying shifts in a LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(uiState.shifts) { shift ->
                    ShiftItem(shift) // Assuming ShiftItem is another composable that displays shift details
                }
            }
        }
    }
}
@Composable
fun ShiftItem(shift: Shift) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Use CardDefaults.elevation

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Shift Date: ${shift.date}")
            Text(text = "Start Time: ${shift.startTime}")
            Text(text = "End Time: ${shift.endTime}")
            Text(text = "Posted By: ${shift.postedBy.username}")
            Text(text = "Current Workers: ${shift.currentWorkers}/${shift.workerLimit}")
        }
    }
}