package com.example.atob.ui.viewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
//import com.example.atob.Manifest
import com.example.atob.ui.state.AddressUiState
import com.example.atob.ui.state.AuthViewUiState
import com.example.atob.ui.state.FindShiftUiState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class AddressViewModel(
    private val geocoder: Geocoder // Inject Geocoder or a repository for geocoding
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddressUiState>(AddressUiState.Success())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()


    fun collectCurrentAddress(context: Context) {
        viewModelScope.launch(Dispatchers.IO) { // Perform work in the background
            try {
                // Indicate loading state
                withContext(Dispatchers.Main) {
                    _uiState.value = AddressUiState.Loading
                }
                Log.d("Geocode", "Loading...")

                // Step 1: Get the device's current location
                val location = getCurrentLocation(context)
                    ?: throw IllegalStateException("Unable to retrieve current location")
                Log.d("Geocode", "Lat: ${location.latitude}, Lon: ${location.longitude}")

                // Step 2: Reverse Geocode the location
                val gpsAddress = reverseGeocode(location.latitude, location.longitude)

                // Update `uiState` with success on the main thread
                withContext(Dispatchers.Main) {
                    Log.d("Geocode", "Address: $gpsAddress")
                    _uiState.value = AddressUiState.Success(
                        gpsAddress = gpsAddress,
                        isMatching = null, // Update this if needed
                         location = LatLng(location.latitude, location.longitude)
                    )
                }
            } catch (e: Exception) {
                // Handle errors and update UI state on the main thread
                withContext(Dispatchers.Main) {
                    _uiState.value = AddressUiState.Error(message = "Error validating address: ${e.localizedMessage}")
                }
            }
        }
    }


    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            Log.d("Geocode", "Address: $addresses")
            if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0) // Get full address line
            } else {
                "Unknown Location"
            }        }
    }

    private suspend fun geocodeAddress(address: String): Pair<Double, Double> {
        return withContext(Dispatchers.IO) {
            val locations = geocoder.getFromLocationName(address, 1)
            if (locations!!.isNotEmpty()) {
            //    Log.d("Geocode", "Lat: $locations.latitude, Lon: $locations.longitude")

                val location = locations[0]
                Pair(location.latitude, location.longitude)

            } else {
                Log.e("Geocode", "Address not found")
                throw IllegalArgumentException("Invalid Address")
            }
        }
    }

    suspend fun compareAddressesWithCurrentLocation(address: String): Boolean{

            try {
                val pair = geocodeAddress(address)
                val givenCoords = (_uiState.value as AddressUiState.Success).location
                val isMatching = compareAddresses(givenCoords!!.latitude, givenCoords.longitude, pair)
                _uiState.value = (_uiState.value as AddressUiState.Success).copy(isMatching = isMatching)
                Log.d("Geocode", "isMatching: $isMatching")

                return isMatching
            } catch (e: Exception) {
                _uiState.value = AddressUiState.Error(message = "Error validating address: ${e.localizedMessage}")
                return false
            }


    }

    private fun compareAddresses(lat1: Double, lon1: Double, givenCoords: Pair<Double, Double>): Boolean {
        val distance = haversine(lat1, lon1, givenCoords.first, givenCoords.second)
        return distance <= 50 // Consider addresses matching if within 50 meters
    }

    suspend fun getCurrentLocation(context: Context): Location? {
    // Check location permissions
    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
        throw SecurityException("Location permissions not granted")
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    return suspendCancellableCoroutine { continuation ->
        // Create a location request
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // Update interval (1 second)
        ).setMaxUpdates(1) // Only one update is needed
            .build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                fusedLocationClient.removeLocationUpdates(this) // Stop updates
                val location = locationResult.lastLocation
                if (location != null) {
                    continuation.resume(location)
                    Log.d("Geocode", "Lat: ${location.latitude}, Lon: ${location.longitude}")
                } else {
                    continuation.resumeWithException(Exception("Failed to fetch current location"))
                }
            }

            override fun onLocationAvailability(locationAvailability: com.google.android.gms.location.LocationAvailability) {
                Log.d("Geocode", "Location availability: ${locationAvailability.isLocationAvailable}")
            }
        }

        // Start location updates with the main thread Looper
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            android.os.Looper.getMainLooper() // Use the main thread Looper
        )

        // Cancel the request if the coroutine is cancelled
        continuation.invokeOnCancellation {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Earth radius in meters
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c // Distance in meters
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val geocoder = Geocoder(application.applicationContext, Locale.getDefault())
                AddressViewModel(geocoder)
            }
        }
    }
}

