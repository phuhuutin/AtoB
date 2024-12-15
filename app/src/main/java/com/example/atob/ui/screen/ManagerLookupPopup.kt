package com.example.atob.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.atob.model.AttendanceRecord
import com.example.atob.model.PayRateUpdateRequest
import com.example.atob.model.User
import com.example.atob.ui.viewModel.AuthViewModel
import kotlinx.coroutines.launch
@Composable
fun ManagerLookupPopup(
    authViewModel: AuthViewModel,
    isOpen: Boolean,
    onClose: () -> Unit,
    currentUser: User?
) {
    if (isOpen) {
        val userDataState = remember { mutableStateOf<User?>(currentUser ?: null) }
        val attendanceState = remember { mutableStateOf<List<AttendanceRecord>>(currentUser?.attendancePoints?.attendanceRecords ?: emptyList()) }
        val newPayRate = remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = {
                onClose()
                authViewModel.clearDialogMessage()
            },
            title = { Text("User Profile", style = MaterialTheme.typography.bodyMedium) },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    userDataState.value?.let { user ->
                        // Display user details
                        Text("Username: ${user.username}", style = MaterialTheme.typography.bodyMedium)
                        Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Pay Rate: ", style = MaterialTheme.typography.bodyMedium)
                            Text(" ${user.payRate}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Edit Pay Rate Section
                        TextField(
                            value = newPayRate.value,
                            onValueChange = { newPayRate.value = it },
                            label = { Text("Edit Pay Rate") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    authViewModel.updatePayRate(PayRateUpdateRequest(newPayRate.value.toDouble(),currentUser!!.id))
                                    //onClose() // Close after saving
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Update Pay Rate", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Attendance Records Section
                    Text("Attendance Records", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(attendanceState.value) { record ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Date: ${record.date}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Reason: ${record.reason}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Shift id: ${record.shiftId}", style = MaterialTheme.typography.bodyMedium)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    // Implement delete logic here
                                                    if(authViewModel.deleteAttendanceRecord(record.id))
                                                        attendanceState.value =
                                                            attendanceState.value.filter { it.id != record.id }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Delete", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClose()
                        authViewModel.clearDialogMessage()
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Close", style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }
}

