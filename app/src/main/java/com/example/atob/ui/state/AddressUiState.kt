package com.example.atob.ui.state

import com.google.android.gms.maps.model.LatLng

sealed class AddressUiState{
    object Loading : AddressUiState()  // Represents the loading state
    data class Success(
        val location: LatLng? = null,
        val gpsAddress: String = "",
        val isMatching: Boolean? = null, // null means not yet validated
    ): AddressUiState()  // Represents the success state
    data class Error(val message: String) : AddressUiState()  // Represents the error state
}



