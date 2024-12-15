package com.example.atob.ui.screen

import ShiftFormPopup
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.atob.model.FindShift
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.state.FindShiftUiState
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.FindShiftViewModel
import com.example.atob.ui.viewModel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
enum class FindShiftStatus{
    Available,
    Full,
    AlreadyAdded;

    @Composable
    fun themeColor(): Color {
        return when (this) {
            Available -> MaterialTheme.colorScheme.primary
            Full -> MaterialTheme.colorScheme.error
            AlreadyAdded -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }


    suspend fun onClick(findShiftViewModel: FindShiftViewModel, shift: FindShift, homeViewModel: HomeViewModel) =
        when(this){
            Available -> {
                val isSusscessful = findShiftViewModel.addShiftSuspend(shift)
                if(isSusscessful)
                    homeViewModel.loadShiftsSuspend() else TODO()

            }

            Full -> {

            }
            AlreadyAdded -> {
                val isSusscessful = findShiftViewModel.dropShiftSuspend(shift.id)
                if(isSusscessful)
                    homeViewModel.loadShiftsSuspend() else{

                }
            }
        }
}
@Composable
fun FindShiftScreen(findShiftViewModel: FindShiftViewModel, modifier: Modifier, authViewModel: AuthViewModel, homeViewModel: HomeViewModel) {
    val currentUser by remember { mutableStateOf((authViewModel.uiState.value as AuthViewUiState.Success).userInfo) }
    val uiState by findShiftViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by rememberSaveable  { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
    val dialogMessage = (uiState as? FindShiftUiState.Success)?.dialogMessage ?: ""
    LaunchedEffect(Unit) {
        findShiftViewModel.getShiftsAfterNow()
    }
    val closeDialog = { showDialog = false }
    val openDialog = { showDialog = true }
    val expandedShiftIds = remember { mutableStateOf(setOf<Long>()) }
    val isExpanded = expandedShiftIds.value.contains(currentUser?.id)
    val expandToggle = { shiftId: Long ->
        expandedShiftIds.value = if (expandedShiftIds.value.contains(shiftId)) {
            expandedShiftIds.value - shiftId
        } else {
            expandedShiftIds.value + shiftId
        }
    }

    // Display dialog if `showDialog` is true
    if (showDialog && dialogMessage.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = "Shift Status") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    closeDialog()
                    findShiftViewModel.clearDialogMessage()
                }) {
                    Text("OK")
                }
            }
        )
    }

    if (showPopup && currentUser != null && currentUser?.role.equals("MANAGER")) {
            ShiftFormPopup(
                onDismiss = { showPopup = false },
                onSave = findShiftViewModel.createShiftSetId(currentUser!!.id, currentUser!!.employer.id),
                openDialog = openDialog,
                closeDialog = closeDialog
            )
    }

    when (uiState) {
        is FindShiftUiState.Loading -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){ CircularProgressIndicator() }
        }
        is FindShiftUiState.Success -> {
            // Display the list of shifts
            val shifts = (uiState as FindShiftUiState.Success).userShifts
            Log.d("FindShiftScreen", (uiState as FindShiftUiState.Success).dialogMessage)
            val shiftMappedbyDate: Map<LocalDate, List<FindShift>> = shifts.groupBy { it.date }
            val dates = shiftMappedbyDate.keys.toList()
            Scaffold(

                floatingActionButton = {
                    if(currentUser?.role.equals("MANAGER")) {
                        FloatingActionButton(
                            onClick = {showPopup = !showPopup},
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            if(currentUser?.role !== "MANAGER")
                                Icon(
                                    imageVector = Icons.Default.Add, // Use default "Add" icon
                                    contentDescription = "Add"
                                )
                        }
                    }

                },
                floatingActionButtonPosition = FabPosition.End // Positioning (default is end)
            ) { paddingValues ->

                if(dates.isEmpty()){
                    Text(text = "No shifts available", color = MaterialTheme.colorScheme.primary)
                }else{
                    Column {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier
                                .fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,  // Background color for the tab row
                            edgePadding = 8.dp,  // Add some padding if desired
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = MaterialTheme.colorScheme.primary // Color for the indicator under the selected tab
                                )
                            }
                        ) {

                            dates.forEachIndexed { index, date ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Color for selected tab content
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f
                                    ), // Color for unselected tab content
                                    modifier = Modifier.padding(4.dp) // Optional padding for tab items
                                ) {
                                    Text(
                                        text = "${date.dayOfWeek} \n ${date.monthValue}/${date.dayOfMonth}",
                                        color = if (selectedTabIndex == index)
                                            MaterialTheme.colorScheme.primary // Brighter color for the selected tab
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Dimmer color for unselected tabs
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }


                        }
                        // Display the shifts corresponding to the selected date
                        val selectedDate = dates[selectedTabIndex]
                        val shiftsForDate = shiftMappedbyDate[selectedDate] ?: emptyList()
                        Text(
                            text = "Shift(${shiftsForDate.size})",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        LazyColumn {
                            items(shiftsForDate) { shift ->
                                // Customize the way you display each FindShift item
                                FindShiftItem(
                                    shift,
                                    authViewModel,
                                    findShiftViewModel,
                                    homeViewModel,
                                    modifier = modifier,
                                    closeDialog = closeDialog,
                                    openDialog = openDialog
                                )
                            }
                        }
                    }
            }
            }
        }
        is FindShiftUiState.Error -> {
            // Show an error message
            val errorMessage = (uiState as FindShiftUiState.Error).message
            Text(text = "Error: $errorMessage", color = Color.Red)
        }
    }

}

@Composable
fun FindShiftItem(shift: FindShift,
                  authViewModel: AuthViewModel,
                  findShiftViewModel: FindShiftViewModel,
                  homeViewModel: HomeViewModel,
                  modifier: Modifier = Modifier,
                  openDialog: () -> Unit = {},
                  closeDialog: () -> Unit = {},
                  isExpanded: Boolean = false,
                  expandToggle: () -> Unit = {}
                  ) {
    // Format the date and times for display
    val uiState by findShiftViewModel.uiState.collectAsState()
     val currentUser by remember { mutableStateOf((authViewModel.uiState.value as AuthViewUiState.Success).userInfo) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val shiftStatus: FindShiftStatus = if(shift.employees.contains(currentUser))
                                                FindShiftStatus.AlreadyAdded
                                            else if(shift.shiftFull)
                                                FindShiftStatus.Full
                                            else
                                                FindShiftStatus.Available
    var isExpanded by remember { mutableStateOf(false) }

    var listOfEmployee: String  = ""
    shift.employees.forEach {
        listOfEmployee += it.username + ", "
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded
                    Log.e("FindShiftItem", "Shift Details: $shift")
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val roundedHours = String.format("%.2f", ((Duration.between(shift.startTime, shift.endTime).toMinutes()).toDouble() / 60)).toDouble()

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${shift.startTime.format(timeFormatter)}  - ${shift.endTime.format(timeFormatter)}  (${roundedHours} h)", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when(shiftStatus){
                            FindShiftStatus.Available -> "Available"
                            FindShiftStatus.Full -> "Full"
                            FindShiftStatus.AlreadyAdded -> "Already Added"
                        },
                        color = shiftStatus.themeColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column (modifier = Modifier.padding(16.dp)){
                    Button(onClick = {
                        homeViewModel.viewModelScope.launch {
                            openDialog()
                            shiftStatus.onClick(findShiftViewModel, shift, homeViewModel)

                        }
                    }, colors = if(shiftStatus == FindShiftStatus.AlreadyAdded) ButtonDefaults. buttonColors(MaterialTheme.colorScheme.error) else
                        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        content = { Text(
                            text = when(shiftStatus){
                                FindShiftStatus.AlreadyAdded -> "Drop"
                                else -> "Pick"
                            })},
                        enabled = (shiftStatus == FindShiftStatus.Available || shiftStatus == FindShiftStatus.AlreadyAdded) ?: false)
                    if(shift.postedBy.equals(currentUser)){
                        Button(onClick = {
                                openDialog()
                                findShiftViewModel.deleteShift(shift.id, homeViewModel::loadShiftsSuspend)
                            },
                             colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                            content = { Text(text = "Delete", style = MaterialTheme.typography.bodyMedium)})

                    }
                }

            }
            // Content
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 0.dp, 24.dp, 24.dp)
                ) {
                    Text(
                        text = "Details:",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Date: ${shift.date.dayOfWeek} ${shift.date.monthValue}/${shift.date.dayOfMonth}/${shift.date.year}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Posted by: ${shift.postedBy.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Workers (${shift.employees.size}/${shift.workerLimit}) : " +
                                "${listOfEmployee}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    }

            }
        }

    }




}