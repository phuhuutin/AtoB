package com.example.atob.ui.screen

import SignUpDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atob.model.ChangePasswordRequest
import com.example.atob.model.User
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier = Modifier, authViewModel: AuthViewModel,     updateNewCredetial:   () -> Unit
) {
    // Use the MaterialTheme color scheme for the screen
    val currentUser by remember { mutableStateOf((authViewModel.uiState.value as AuthViewUiState.Success).userInfo) }
    var isChangePasswordDialogOpen by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val uiState by authViewModel.uiState.collectAsState()
    val resultMessage = (uiState as? AuthViewUiState.Success)?.dialogMessage ?: ""
    val coroutineScope = rememberCoroutineScope()
    var signUpDialogOpen by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<String>("") }
    var showPopup by remember { mutableStateOf(false) }
    val lookUpUser = remember { mutableStateOf<User?>(null) }


    //Sign up dialog

    if (signUpDialogOpen) {
        SignUpDialog(
            onDismiss = {
                signUpDialogOpen = false },
            onSignUp = { signUpRequest ->
                showDialog = true
                authViewModel.signUp(signUpRequest)
            },
            employerId = currentUser?.employer!!.id
        )
    }



    // Show Dialog with loading or result message
    if (showDialog || resultMessage.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            }, // Close dialog
            title = { Text("Profile") },
            text = {
                if (showDialog && resultMessage.isBlank()) {
                    Text("Please wait...") // Loading message
                } else {
                    Text(resultMessage ?: "") // Display result or error
                }
            },
            confirmButton = {
                Button(onClick = {

                    authViewModel.clearDialogMessage()
                    showDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }


    ChangePasswordDialog(
        isDialogOpen = isChangePasswordDialogOpen,
        onDismiss = { isChangePasswordDialogOpen = false },
        onSubmit = { oldPassword, newPassword ->
            coroutineScope.launch {
                authViewModel.changePassword(ChangePasswordRequest(currentUser?.username!!, oldPassword, newPassword))
                updateNewCredetial()
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        bottomBar = {


        },
        content = { paddingValues ->

            ManagerLookupPopup(authViewModel, showPopup, { showPopup = false }, lookUpUser.value)


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    if (currentUser?.role == "MANAGER") {
                        TextField(
                            value = selectedUserId?.toString() ?: "",
                            onValueChange = {  selectedUserId = it },
                            label = { Text("Look up User by Id or Username") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        val user =
                                            authViewModel.findUserByIdOrUsername(selectedUserId)
                                        if (user != null) {
                                            lookUpUser.value = user
                                            showPopup = true
                                        }else{
                                            showDialog = true
                                        }


                                    }
                                    }){
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.id.toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("ID: ${currentUser?.id}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Username: ${currentUser?.username}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Employer: ${currentUser?.employer?.name}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Email: ${currentUser?.email}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pay Rate: $${currentUser?.payRate}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Role: ${currentUser?.role}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    Column {
                        if (currentUser?.role == "MANAGER") {
                            Button(
                                onClick = { signUpDialogOpen = true },
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                            ) {
                                Text("Add a New Worker", color = MaterialTheme.colorScheme.onSecondary)
                            }
                        }

                        Button(
                            onClick = { isChangePasswordDialogOpen = true },
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                        ) {
                            Text("Change Password", color = MaterialTheme.colorScheme.onSecondary)
                        }

                        Button(
                            onClick = {
                                authViewModel.logout()
                                navController.navigate("login")
                            },
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                        ) {
                            Text("Logout", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }

        }
    )
}