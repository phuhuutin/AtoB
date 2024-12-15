package com.example.atob.ui.state

import com.example.atob.model.User

sealed class AuthViewUiState {
    object Initial : AuthViewUiState()  // Represents the initial state
    object Loading : AuthViewUiState()  // Represents the loading state
    data class Success(val userInfo: User?, var dialogMessage: String = "", var loginError: String = "") : AuthViewUiState()  // Represents the success state
    data class Error(val message: String) : AuthViewUiState()  // Represents the error state
}