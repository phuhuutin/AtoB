package com.example.atob.ui.screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.atob.model.ClockInOutRecordsUpdate
import com.example.atob.model.Report
import com.example.atob.model.ReportType
import com.example.atob.model.SimpleShift
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.state.ReportUiState
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.ReportViewModel

@Composable
fun ReportScreen(authViewModel: AuthViewModel, reportViewModel: ReportViewModel){
    val uiState by reportViewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable  { mutableIntStateOf(0) }
    val authUiState by authViewModel.uiState.collectAsState()
    val user = (authUiState as? AuthViewUiState.Success)?.userInfo
    var showDialog by remember { mutableStateOf(false) }
    val resultMessage = (uiState as? ReportUiState.Success)?.message
    val openDialog = {
        showDialog = true
    }
    val closeDialog = {
        showDialog = false

    }

    LaunchedEffect(Unit) {
        reportViewModel.getAllReports(user)
        if(uiState is ReportUiState.Success){
            Log.e("ReportScreen", "uiState: ${(uiState as ReportUiState.Success).reports}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {

            }, // Close dialog
            title = { Text("Report Creation") },
            text = {
                if (showDialog && uiState is ReportUiState.Loading) {
                    Text("Please wait...") // Loading message
                } else {
                    Text(resultMessage ?: "") // Display result or error
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    reportViewModel.clearMessage()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs for Incompleted and Completed reports
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            ) {
                Text("Incompleted Reports", modifier = Modifier.padding(16.dp))
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1  }
            ) {
                Text("Completed Reports", modifier = Modifier.padding(16.dp))
            }
        }

        if(uiState is ReportUiState.Loading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }else{
            // Show reports based on the selected tab and the user role
            when (selectedTabIndex) {
                0 -> {
                    val reports = (uiState as? ReportUiState.Success)?.reports?.filter { !it.completed }
                    if(reports.isNullOrEmpty()){
                        Text("No Incompleted Reports")
                    }else{
                        LazyColumn {
                            items(reports) { report ->
                                InCompletedReportCard(report = report, reportViewModel = reportViewModel, openDialog = openDialog)
                            }
                        }
                    }
                }
                1 -> {
                    val reports = (uiState as? ReportUiState.Success)?.reports?.filter { it.completed }
                    if(reports.isNullOrEmpty()){
                        Text("No Completed Reports")
                    }else {
                        LazyColumn {
                            items(reports) { report ->
                                CompletedReportCard(report = report, reportViewModel = reportViewModel, openDialog = openDialog)
                            }
                        }
                    }
                }

            }
        }





    }



}

@Composable
fun InCompletedReportCard(report: Report, reportViewModel: ReportViewModel, openDialog: () -> Unit) {
    // State to track whether the card is expanded
    var isExpanded by remember { mutableStateOf(false) }
    // Animate the rotation of the arrow icon
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }, // Toggle expanded state on click
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Title and Arrow Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Report Title
                Column {
                    Text(
                        text = "Report Type: ${report.type}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${report.reportDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Arrow Icon to indicate expanded state
                Icon(
                    imageVector =  Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Status: Completed or Incompleted
            Text(
                text = if (report.completed) "Status: Completed" else "Status: Incompleted",
                style = MaterialTheme.typography.bodyMedium,
                color = if (report.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Report Details
            Text(
                text = "Details: ${report.details}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Expanded content
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Additional Details:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Display extra details based on the report type
                when (report.type) {
                    ReportType.CLOCK -> {
                        if(report.shift != null)
                            ShiftItemEditable(report.shift, reportViewModel = reportViewModel, openDialog = openDialog, clockId = report.clock.id, reportId = report.id)
                    }
                    ReportType.ATTENDANCE -> {
                        if(report.attendanceRecord != null)
                            AttendanceRecordItem(report.attendanceRecord)
                    }
                    else -> {
                        Text("No additional details available.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
@Composable
fun CompletedReportCard(report: Report, reportViewModel: ReportViewModel, openDialog: () -> Unit) {
    // State to track whether the card is expanded
     // Animate the rotation of the arrow icon
     Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
         elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Title and Arrow Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Report Title
                Column {
                    Text(
                        text = "Report Type: ${report.type}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${report.reportDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Arrow Icon to indicate expanded state
                Icon(
                    imageVector =  Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Status: Completed or Incompleted
            Text(
                text = if (report.completed) "Status: Completed" else "Status: Incompleted",
                style = MaterialTheme.typography.bodyMedium,
                color = if (report.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Report Details
            Text(
                text = "Details: ${report.details}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ShiftItemEditable(userShift: SimpleShift, modifier: Modifier = Modifier, reportViewModel: ReportViewModel,
                      openDialog: () -> Unit,
                      clockId: Long, reportId: Long) {
    // Local state for hours and minutes
    var startHour by remember { mutableStateOf(userShift.startTime.hour.toString()) }
    var startMinute by remember { mutableStateOf(userShift.startTime.minute.toString()) }
    var endHour by remember { mutableStateOf(userShift.endTime.hour.toString()) }
    var endMinute by remember { mutableStateOf(userShift.endTime.minute.toString()) }

    var startTimeError by remember { mutableStateOf(false) }
    var endTimeError by remember { mutableStateOf(false) }

    Text(
        text = "Shift Details (id: ${userShift.id})",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Start Time Inputs
            Text(
                text = "Start Time:",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = startHour,
                    onValueChange = { newValue ->
                        startTimeError = false
                        if (newValue.all { it.isDigit() }) {
                            startHour = newValue
                        }
                    },
                    label = { Text("Hours") },
                    isError = startTimeError,
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                Text(":")
                TextField(
                    value = startMinute,
                    onValueChange = { newValue ->
                        startTimeError = false
                        if (newValue.all { it.isDigit() }) {
                            startMinute = newValue
                        }
                    },
                    label = { Text("Minutes") },
                    isError = startTimeError,
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            // End Time Inputs
            Text(
                text = "End Time:",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = endHour,
                    onValueChange = { newValue ->
                        endTimeError = false
                        if (newValue.all { it.isDigit() }) {
                            endHour = newValue
                        }
                    },
                    label = { Text("Hours") },
                    isError = endTimeError,
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                Text(":")
                TextField(
                    value = endMinute,
                    onValueChange = { newValue ->
                        endTimeError = false
                        if (newValue.all { it.isDigit() }) {
                            endMinute = newValue
                        }
                    },
                    label = { Text("Minutes") },
                    isError = endTimeError,
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Submit Button
                Button(
                    onClick = {
                        try {
                            val startHourInt = startHour.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid start hour")
                            val startMinuteInt = startMinute.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid start minute")
                            val endHourInt = endHour.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid end hour")
                            val endMinuteInt = endMinute.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid end minute")

                            if (startHourInt in 0..23 && startMinuteInt in 0..59) {
                                userShift.startTime = userShift.startTime
                                    .withHour(startHourInt)
                                    .withMinute(startMinuteInt)
                            } else {
                                startTimeError = true
                            }

                            if (endHourInt in 0..23 && endMinuteInt in 0..59) {
                                userShift.endTime = userShift.endTime
                                    .withHour(endHourInt)
                                    .withMinute(endMinuteInt)
                            } else {
                                endTimeError = true
                            }
                            if (!startTimeError && !endTimeError) {
                                openDialog()
                                reportViewModel.updateClock(
                                    ClockInOutRecordsUpdate(
                                        id = clockId,
                                        clockInTime = userShift.startTime.withHour(startHourInt)
                                            .withMinute(startMinuteInt),
                                        clockOutTime = userShift.endTime.withHour(endHourInt)
                                            .withMinute(endMinuteInt)
                                    ),
                                    reportId
                                )
                            }
                        } catch (e: Exception) {
                            // Handle errors gracefully
                            startTimeError = true
                            endTimeError = true
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Submit")
                }

                Button(
                    onClick = {
                        openDialog()
                        reportViewModel.closeReport(reportId)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Close")
                }
            }

            // Error Messages
            if (startTimeError) {
                Text(
                    text = "Invalid start time. Hours must be 0-23, minutes 0-59.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (endTimeError) {
                Text(
                    text = "Invalid end time. Hours must be 0-23, minutes 0-59.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}