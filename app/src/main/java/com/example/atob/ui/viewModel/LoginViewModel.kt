package com.example.atob.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.NetworkUserRepository
import com.example.atob.data.UserRepository
import com.example.atob.model.LoginRequest
import com.example.atob.model.User

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun login(username: String, password: String): User {
       return  userRepository.login( LoginRequest(username, password))
    }

    /**
     * Factory for [LoginViewModel] that takes [NetworkUserRepository] as a dependency
     */
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkUserRepository = application.container.userRepository
                LoginViewModel(networkUserRepository)
            }
        }
    }
}