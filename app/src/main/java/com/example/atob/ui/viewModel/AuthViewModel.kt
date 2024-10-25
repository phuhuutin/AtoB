package com.example.atob.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.NetworkUserRepository
import com.example.atob.data.UserRepository
import com.example.atob.model.LoginRequest
import com.example.atob.model.User
import com.example.atob.ui.state.AuthViewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _uiState = MutableStateFlow(AuthViewUiState())
    val uiState: StateFlow<AuthViewUiState> get() = _uiState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.login(LoginRequest(username, password))
                _uiState.value = AuthViewUiState(isAuthenticated = true, userInfo = user, loginError = null)
            } catch (e: Exception) {
                _uiState.value = AuthViewUiState(isAuthenticated = false, userInfo = null, loginError = e.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
             _uiState.value = AuthViewUiState() // Reset state on logout
        }
    }

    /**
     * Factory for [AuthViewModel] that takes [NetworkUserRepository] as a dependency
     */
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkUserRepository = application.container.userRepository
                AuthViewModel(networkUserRepository)
            }
        }
    }
}