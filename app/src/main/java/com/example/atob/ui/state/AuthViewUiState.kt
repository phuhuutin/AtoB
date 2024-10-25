package com.example.atob.ui.state

import com.example.atob.model.User

data class AuthViewUiState(
    val isAuthenticated: Boolean = false,
    val userInfo: User? = null,
    val loginError: String? = null
)