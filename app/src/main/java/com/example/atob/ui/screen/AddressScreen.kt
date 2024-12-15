package com.example.atob.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atob.LocationConfirmationDialog
import com.example.atob.ui.state.AddressUiState
import com.example.atob.ui.viewModel.AddressViewModel

@Composable
fun AddressScreen(addressViewModel: AddressViewModel  = viewModel(factory = AddressViewModel.Factory)
) {
    var showDialog by remember { mutableStateOf(false) }
    val uiState by addressViewModel.uiState.collectAsState()
    val context = LocalContext.current


    if (showDialog) {
        LocationConfirmationDialog(
            onConfirm = {
                // Handle user confirmation here (e.g., save the location)
                showDialog = false
            },
            onDismiss = {
                showDialog = false

            },
            addressViewModel = addressViewModel
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showDialog = true
                Log.d("Geocode", "showDialog is true")
                addressViewModel.collectCurrentAddress(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate Address")
        }

        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))

        if ((uiState as? AddressUiState.Success?)?.gpsAddress?.isNotBlank() == true) {
            Text("Current GPS Address: ${(uiState as AddressUiState.Success).gpsAddress}", style = MaterialTheme.typography.bodyMedium)
        }
    }
    }





