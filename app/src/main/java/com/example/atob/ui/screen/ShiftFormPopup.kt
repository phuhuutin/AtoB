import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat.Style
import com.example.atob.R
import com.example.atob.model.ShiftDTO
import com.example.atob.ui.state.FindShiftUiState
import com.example.atob.ui.viewModel.FindShiftViewModel
import com.example.atob.ui.viewModel.HomeViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter



@Composable
fun ShiftFormPopup(onDismiss: () -> Unit, onSave: (ShiftDTO: ShiftDTO) -> Unit, openDialog: () -> Unit, closeDialog: () -> Unit) {
    // State variables for each DTO field
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedStartTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedEndTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var workerLimit by remember { mutableStateOf("1") }
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Show DatePickerDialog
    val showDatePicker = {
        val today = LocalDate.now()
        DatePickerDialog(context, R.style.CustomDatePickerTheme, { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        }, today.year, today.monthValue - 1, today.dayOfMonth).show()
    }

    // Show TimePickerDialog
    val showTimePicker = { onTimeSelected: (LocalTime) -> Unit ->
        val now = LocalTime.now()
        TimePickerDialog(context, R.style.CustomDatePickerTheme, { _, hour, minute ->
            onTimeSelected(LocalTime.of(hour, minute))
        }, now.hour, now.minute, true).show()
    }

    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDate != null && workerLimit.isNotEmpty()) {
                    val shiftDTO = ShiftDTO(
                        date = selectedDate!!,
                        startTime = selectedStartTime,
                        endTime = selectedEndTime,
                        workerLimit = workerLimit.toInt(),

                    )

                        openDialog()
                        onSave(shiftDTO)
                        onDismiss()


                }
                        },
                enabled = selectedStartTime != null && selectedEndTime != null
                        && selectedEndTime?.isAfter(selectedStartTime) == true
                        && workerLimit.toIntOrNull()?.takeIf { it > 0 } != null
                        && (selectedDate?.isAfter(LocalDate.now()) == true ||
                        selectedDate == LocalDate.now())
                        && selectedDate?.minusYears(1L)?.isBefore(LocalDate.now()) == true
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        title = { Text("Create Shift") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Date Picker
                TextButton(onClick = showDatePicker) {
                    Text(text = selectedDate?.format(dateFormatter) ?: "Select Date")
                }
                // Start Time Picker
                TextButton(onClick = { showTimePicker { time ->
                    selectedStartTime = LocalDateTime.of(selectedDate ?: LocalDate.now(), time)
                } }) {
                    Text(text = selectedStartTime?.format(timeFormatter) ?: "Select Start Time")
                }

                // End Time Picker
                TextButton(onClick = { showTimePicker { time ->
                    selectedEndTime = LocalDateTime.of(selectedDate ?: LocalDate.now(), time)
                } }) {
                    Text(text = selectedEndTime?.format(timeFormatter) ?: "Select End Time")
                }

                // Worker Limit Input
                OutlinedTextField(
                    value = workerLimit,
                    onValueChange = { workerLimit = it },
                    label = { Text("Worker Limit") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(16.dp))
                if(selectedDate != null && selectedDate?.isBefore(LocalDate.now()) == true){
                    Text(text = "Can not select date in the past", style =  MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                } else if (selectedStartTime != null && selectedEndTime != null){
                    if (selectedEndTime?.isBefore(selectedStartTime) == true) {
                        Text(text = "End time must be after start time", style =  MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                } else if (workerLimit.isNotBlank() && !(workerLimit.toIntOrNull()?.takeIf { it > 0 } != null)){
                        Text(text = "Worker limit must be greater than 0", style =  MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                } else if (selectedDate?.minusYears(1L)?.isAfter(LocalDate.now()) == true){
                    Text(text = "Date is too far in the future", style =  MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )


}
