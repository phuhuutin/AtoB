package com.example.atob.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.atob.model.ChangePasswordRequest

@Composable
fun ChangePasswordDialog(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (oldPassword: String,
    newPassword : String ) -> Unit
) {
    // State for form fields
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    if (isDialogOpen) {
        // Dialog with content
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Change Password")
            },
            text = {
                Column {
                    // Old Password TextField
                    TextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Old Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // New Password TextField
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Validate and submit the password change request
                        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                            onSubmit(oldPassword, newPassword)
                            onDismiss() // Close the dialog after submit
                        } else {
                            // Handle form validation errors (e.g., show a Toast or Snackbar)
                        }
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}