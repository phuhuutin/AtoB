package com.example.atob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atob.model.Report
import com.example.atob.model.ReportDTO
import com.example.atob.model.ReportType
import java.time.LocalDate

@Composable
fun ReportCreationDialog(
    onDismiss: () -> Unit,
    onCreate: (ReportDTO) -> Unit,
    reportType: ReportType
) {
    // State variables for form fields
     val date by remember { mutableStateOf(LocalDate.now()) }
    var details by remember { mutableStateOf("") }
    val completed by remember { mutableStateOf(false) }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Create New Report", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Report Type Dropdown
                Text("Report Type", style = MaterialTheme.typography.labelMedium)
                TextField(
                    value = reportType.toString(),
                    onValueChange = { /* Date selection logic (or use a DatePicker) */ },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Report Date
                Text("Report Date", style = MaterialTheme.typography.labelMedium)
                TextField(
                    value = date.toString(),
                    onValueChange = { /* Date selection logic (or use a DatePicker) */ },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Report Details
                Text("Details(1024 characters)", style = MaterialTheme.typography.labelMedium)
                TextField(
                    value = details,
                    onValueChange = { if (it.length <= 1024) details = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))



            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Create a new Report object
                    val newReport = ReportDTO(
                        type = reportType,
                        details = details,
                        clockInOutRecordId = 0,
                        attendanceRecordId = 0,
                     )
                    onCreate(newReport)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

