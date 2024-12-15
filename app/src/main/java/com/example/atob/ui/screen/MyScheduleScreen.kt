package com.example.atob.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atob.LocationConfirmationDialog
import com.example.atob.model.ReportDTO
import com.example.atob.model.ReportType
import com.example.atob.model.UserShift
import com.example.atob.model.ClockInOutRecord
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.state.HomeViewUiState
import com.example.atob.ui.state.ReportUiState
import com.example.atob.ui.viewModel.AddressViewModel
import com.example.atob.ui.viewModel.AuthViewModel
import com.example.atob.ui.viewModel.HomeViewModel
import com.example.atob.ui.viewModel.ReportViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ShiftDetails(
    userShifts: List<UserShift>,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    openDialog: () -> Unit = {},
    closeDialog: () -> Unit = {},
    onClockIn: (shiftId: Long) -> Unit = {},
    onCreateReport: (report: ReportDTO) -> Unit,
    reportViewModel: ReportViewModel
) {
    if (userShifts.isEmpty()) {
        Text(
            "No shifts for the selected date",
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        Column(modifier = modifier.padding(horizontal = 16.dp)) {
            userShifts.forEach { shift ->
                ShiftItem(shift, modifier = modifier.padding(top = 10.dp))

                PunchTimes(clock = shift.clock, modifier = modifier.padding(top = 10.dp), shiftId = shift.id, onClockIn = onClockIn, reportViewModel = reportViewModel)

            }
        }
    }
}

@Composable
fun ShiftItem(userShift: UserShift, modifier: Modifier = Modifier) {
    Text(text = "Shift Details (id: ${userShift.id})",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier)
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
            // Row for Start Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start Time:",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = userShift.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Row for End Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "End Time:",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = userShift.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
@Composable
fun PunchTimes(
    clock: ClockInOutRecord? = null,
    modifier: Modifier = Modifier,
    shiftId: Long,
    onClockIn: (shiftId: Long) -> Unit = {},
    reportViewModel: ReportViewModel
    ){

    var showDialog by remember { mutableStateOf(false) }

    // State to manage the loading and result
    val uiState by reportViewModel.uiState.collectAsState()
    var resultMessage by remember { mutableStateOf<String>("") }
    if(uiState is ReportUiState.Success)
        resultMessage = (uiState as ReportUiState.Success).message
    else if(uiState is ReportUiState.Error)
        resultMessage = (uiState as ReportUiState.Error).message

    var showReportDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
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


    // Show the dialog
    if (showReportDialog) {
        ReportCreationDialog(
            onDismiss = { showReportDialog = false },
            onCreate = { newReport ->
                showReportDialog = false
                showDialog = true
                newReport.clockInOutRecordId = clock!!.id
                newReport.shiftId = shiftId
                reportViewModel.createReport(newReport)
                // Pass the new report to your ViewModel or backend logic
             },
            reportType = ReportType.CLOCK
        )
    }



    // Coroutine scope for launching the suspend function
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Punch Times (id: ${clock?.id})",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier)
        Text(text = "Missing Punch Times?",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.clickable{
                showReportDialog = true // Activate the dialog
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {


            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "In  : ${
                        if (clock?.clockInTime != null)
                            clock.clockInTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        else
                            "Not clocked in yet"

                    }",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Out: ${
                        if (clock?.clockOutTime != null)
                            clock.clockOutTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        else
                            "Not clocked out yet"
                    }", color = MaterialTheme.colorScheme.onSurface
                )
            }
            Button(
                onClick = {
                    onClockIn(shiftId) },
                modifier = Modifier.padding(16.dp),
                enabled = clock?.clockOutTime == null){
                    Text(text = "Clock")
                }


        }
    }


}

@Composable
fun MyScheduleScreen(addressViewModel: AddressViewModel = viewModel(factory = AddressViewModel.Factory), homeViewModel: HomeViewModel, authViewModel: AuthViewModel, modifier: Modifier = Modifier, selectedDate: LocalDate? = LocalDate.now(), onDateSelected: (LocalDate) -> Unit = {}, reportViewModel: ReportViewModel) {
    val uiState by homeViewModel.uiState.collectAsState()
    val currentUser by remember { mutableStateOf((authViewModel.uiState.value as? AuthViewUiState.Success)?.userInfo) }
    val daysOfWeek = remember { daysOfWeek() }
    var showDialog by remember { mutableStateOf(false) }
    val resultMessage = (uiState as? HomeViewUiState.Success)?.dialogMessage ?: ""
    val startMonth: Int = (uiState as? HomeViewUiState.Success)?.startMonth?.monthValue ?: LocalDate.now().monthValue
    val endMonth: Int = (uiState as? HomeViewUiState.Success)?.endMonth?.monthValue ?: LocalDate.now().monthValue
    val startOffset: Long = if (LocalDate.now().monthValue - startMonth >= 0)
                                    (LocalDate.now().monthValue - startMonth).toLong()
                            else (LocalDate.now().monthValue - startMonth + 12).toLong()
    val endOffset: Long = if (endMonth - LocalDate.now().monthValue >= 0)
                                (endMonth - LocalDate.now().monthValue).toLong()
                            else (endMonth - LocalDate.now().monthValue + 12).toLong()
    var showClockInDialog by remember { mutableStateOf(false) }
    val addressUIState by addressViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedShiftId by remember { mutableStateOf<Long?>(null) }
    val viewScope = rememberCoroutineScope()

    val onCreateReport: (report: ReportDTO) -> Unit = { report ->

        reportViewModel.createReport(report)
    }

    val  onClockInConFirm : suspend (shiftId: Long) -> Unit = { shiftId ->
        if (addressViewModel.compareAddressesWithCurrentLocation(currentUser!!.employer.toString())) {
            homeViewModel.clockInOut(shiftId)
        } else {
            showDialog = true;
            homeViewModel.setDialMessage("You can not clock in/out, because your location is too far")
        }
    }


    if (!showDialog && showClockInDialog) {
        LocationConfirmationDialog(
            onConfirm = {
                viewScope.launch {
                    onClockInConFirm(selectedShiftId!!)
                    showClockInDialog = false
                }
                // Handle user confirmation here (e.g., save the location)

            },
            onDismiss = {
                showClockInDialog = false

            }
        )
    }

    val onClockIn: (shiftId: Long) -> Unit= { shiftId ->
        selectedShiftId = shiftId
        showClockInDialog = true
        addressViewModel.collectCurrentAddress(context)
    }



    val state = rememberCalendarState(
//        startMonth = YearMonth.now().minusMonths(1),
        startMonth = YearMonth.now().minusMonths(startOffset),
        endMonth = YearMonth.now().plusMonths(endOffset),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = daysOfWeek.first()
    )

    val openDialog = { showDialog = true }
    val closeDialog = { showDialog = false }

    // Show Dialog with loading or result message
    if (showDialog || resultMessage.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {

            }, // Close dialog
            title = { Text("Clock In/Out") },
            text = {
                if (showDialog && resultMessage.isBlank()) {
                    Text("Please wait...") // Loading message
                } else {
                    Text(resultMessage ?: "") // Display result or error
                }
            },
            confirmButton = {
                Button(onClick = {
                    closeDialog()
                    homeViewModel.clearDialogMessage()
                    //    homeViewModel.loadShifts()
                }) {
                    Text("OK")
                }
            }
        )
    }


    when(uiState){
        is HomeViewUiState.Loading -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){ CircularProgressIndicator() }
        }
        is HomeViewUiState.Error -> {
            Text(text = "Error: ${(uiState as HomeViewUiState.Error).errorMessage}")
        }
        else -> {
            val shifts = (uiState as HomeViewUiState.Success).shifts
            LazyColumn  {
                item {
                    HorizontalCalendar(
                        state = state,
                        dayContent = { Day(it, shifts, onDateSelected = onDateSelected, selected = selectedDate == it.date)  },
                        monthHeader = { month ->
                            MonthHeader(daysOfWeek = daysOfWeek, month = month.yearMonth.toString(),
                                username = currentUser?.username ?: "No username"
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item{
                    Text(text = "${selectedDate!!.dayOfWeek.name},  ${selectedDate.month.name} ${selectedDate.dayOfMonth}", color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                item {
                    ShiftDetails(userShifts = shifts.getOrDefault(selectedDate, emptyList()), modifier = modifier, homeViewModel = homeViewModel, onClockIn = onClockIn,  onCreateReport = onCreateReport, openDialog = openDialog, closeDialog = closeDialog, reportViewModel = reportViewModel)

                }



            }
        }
    }

}



/**
 * Calendar Composes
 */

@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>, month: String, modifier: Modifier = Modifier, username: String = "") {
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = month, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp))

        }
        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
    }
}

@SuppressLint("NewApi")
@Composable
fun Day(day: CalendarDay, shifts: Map<LocalDate, List<UserShift>>, onDateSelected: (LocalDate) -> Unit, selected: Boolean = false, modifier: Modifier = Modifier) {
    // Memoized calculation of markColor

    val markColor = remember(shifts) {
        when {
            !shifts.containsKey(day.date) -> MarkColor.NONE
            shifts.getValue(day.date).isEmpty() -> MarkColor.NONE

            day.date.isBefore(LocalDate.now()) -> {
                // Check if there are any shifts without a clock
                if (shifts[day.date]?.any { it.clock == null } == true) {
                    MarkColor.ERROR
                } else {
                    MarkColor.SECONDARY
                }
            }
            else -> MarkColor.PRIMARY
        }
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(markColor.backgroundColor())
            .border(
                width = 2.dp,
                color = if (selected) Color.Blue else Color.Gray, // Change border color based on selection
            )
            .shadow(
                elevation = 1.dp, // Adjust elevation for shadow size
            )
            .clickable {

                onDateSelected(day.date)

            },
        contentAlignment = Alignment.Center,

        ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = markColor.textColor(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}