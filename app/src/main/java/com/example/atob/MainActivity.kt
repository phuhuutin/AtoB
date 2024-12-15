package com.example.atob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atob.ui.AppNavigation
import com.example.atob.ui.screen.AddressScreen
import com.example.atob.ui.state.AddressUiState
import com.example.atob.ui.theme.AtoBTheme
import com.example.atob.ui.viewModel.AddressViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtoBTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppNavigation()
                  // AddressScreen()
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    AtoBTheme {
//        val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
//        val navController = rememberNavController()
//
//        LoginScreen(
//            navController = navController,
//            authViewModel = authViewModel,
//            homeViewModel = homeViewModel,
//            findShiftViewModel = findShiftViewModel
//        )
//    }
//}



@Composable
fun LocationConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
     addressViewModel: AddressViewModel = viewModel(factory = AddressViewModel.Factory)
) {
    val uiState by addressViewModel.uiState.collectAsState()
    val successState = uiState as? AddressUiState.Success

    val location = (uiState as? AddressUiState.Success)?.location ?: LatLng(0.0, 0.0)


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
            elevation =  CardDefaults.cardElevation(8.dp)
        ) {
            Text("Google Map", modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.titleLarge)
            Column( ) {
                // Google Map inside Card with size constraints
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Set height of the GoogleMap
                ) {
                    when(uiState){
                        is AddressUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp), // Set height of the GoogleMap
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Text("Loading your location...")
                                }

                            }

                        }
                        is AddressUiState.Success -> {

                            if(location != LatLng(0.0, 0.0))
                            GoogleMap(
                                cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(location, 15f) // Increased zoom level for better view
                                },
                                modifier = Modifier.fillMaxSize().padding(6.dp)
                            ) {
                                // Marker at the location
                                Marker(
                                    state = MarkerState(position = location),
                                    title = "San Francisco"
                                )
                            }


                        }
                        is AddressUiState.Error ->{
                            Text((uiState as AddressUiState.Error).message.toString())
                        }
                    }

                }

                if(uiState is AddressUiState.Success || uiState is AddressUiState.Error){
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
                        if(uiState is AddressUiState.Success)
                            Button(onClick = onConfirm) {
                                Text("Confirm Location")
                            }
                    }
                }
            }
        }
    }
}