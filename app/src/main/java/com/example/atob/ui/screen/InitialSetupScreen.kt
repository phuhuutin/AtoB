package com.example.atob.ui.screen

import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.atob.model.InitialSetupDTO
import com.example.atob.ui.viewModel.AuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialSetupScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onSetupComplete: suspend (InitialSetupDTO) -> Unit,
    onAddressConfirm: (String) -> Unit // Callback for address confirmation
) {
    // Manage form state
    var initialSetup by remember { mutableStateOf(InitialSetupDTO()) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current



    if (showDialog) {
        LocationConfirmationDialog(
            onConfirm = {
                showDialog = false
                // Handle user confirmation here (e.g., save the location)
            },
            onDismiss = {
                showDialog = false
            },
            addressString = initialSetup.street + ", " + initialSetup.city + ", " + initialSetup.state + ", " + initialSetup.postalCode + ", " + initialSetup.country
        )
    }
    // Scaffold with App Bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Initial Setup") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Employer Name
                item {
                    TextField(
                        value = initialSetup.employerName!!,
                        onValueChange = { initialSetup = initialSetup.copy(employerName = it) },
                        label = { Text("Employer Name") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Address Fields
                item {
                    TextField(
                        value = initialSetup.street!!,
                        onValueChange = { initialSetup = initialSetup.copy(street = it) },
                        label = { Text("Street") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    TextField(
                        value = initialSetup.city!!,
                        onValueChange = { initialSetup = initialSetup.copy(city = it) },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = initialSetup.state!!,
                            onValueChange = { initialSetup = initialSetup.copy(state = it) },
                            label = { Text("State") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        TextField(
                            value = initialSetup.postalCode!!,
                            onValueChange = { initialSetup = initialSetup.copy(postalCode = it) },
                            label = { Text("Postal Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    TextField(
                        value = initialSetup.country!!,
                        onValueChange = { initialSetup = initialSetup.copy(country = it) },
                        label = { Text("Country") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Button(
                        onClick = {
//                            val fullAddress = "${initialSetup.street}, ${initialSetup.city}, " +
//                                    "${initialSetup.state}, ${initialSetup.postalCode}, ${initialSetup.country}"
//                            onAddressConfirm(fullAddress)

                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Address")
                    }
                }

                // Username and Password
                item {
                    TextField(
                        value = initialSetup.username!!,
                        onValueChange = { initialSetup = initialSetup.copy(username = it) },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    TextField(
                        value = initialSetup.password!!,
                        onValueChange = { initialSetup = initialSetup.copy(password = it) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Email
                item {
                    TextField(
                        value = initialSetup.email!!,
                        onValueChange = { initialSetup = initialSetup.copy(email = it) },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Submit Button
                item {
                    Button(
                        onClick = {
                            coroutineScope.launch{
                                val latLong: LatLng = getLatLngFromAddress(initialSetup.street + ", " + initialSetup.city + ", " + initialSetup.state + ", " + initialSetup.postalCode + ", " + initialSetup.country, context)
                                initialSetup = initialSetup.copy(latitude = latLong.latitude, longitude = latLong.longitude)
                                authViewModel.setUpnewEmployer(initialSetup)
                                onSetupComplete(initialSetup)
                            }
                            navController.popBackStack() // Navigate back
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    )
}


@Composable
fun LocationConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    addressString: String // Address string passed as parameter
) {
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Launch a coroutine to get the LatLng based on the addressString
    LaunchedEffect(addressString) {
        isLoading = true
        errorMessage = null
        location = getLatLngFromAddress(addressString, context) // Assuming you have a function that converts address to LatLng
        isLoading = false
    }

    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp), // Rounded corners for the Card
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Google Map",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Address: $addressString", // Display the full address
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Set height of the GoogleMap
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            Text("Loading your location...")
                        }
                    } else if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        GoogleMap(
                            cameraPositionState = rememberCameraPositionState  {
                                position = CameraPosition.fromLatLngZoom(location, 15f) // Zoom level for better view
                            },
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        ) {
                            Marker(
                                state = MarkerState(position = location),
                                title = "Location"
                            )
                        }
                    }
                }

                // Confirmation and Dismiss buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirm()
                        },
                        enabled = location != LatLng(0.0, 0.0) // Enable button if location is valid
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }
    }
}

// This function should call the Geocoding API and return a LatLng object based on the address string.
suspend fun getLatLngFromAddress(address: String, context: android.content.Context): LatLng {
    // Replace this with actual geocoding logic
    // For example, using Google's Geocoding API:
    val geocoder = Geocoder(context) // Get the context or pass it as a parameter
    val addresses = geocoder.getFromLocationName(address, 1)
    if (addresses != null) {
        if (addresses.isNotEmpty()) {
            val location = addresses[0]
            return  LatLng(location.latitude, location.longitude)
        }else {
            return LatLng(0.0, 0.0) // Return a default invalid location if not found
        }
    }else {
        return  LatLng(0.0, 0.0) // Return a default invalid location if not found
    }
}
