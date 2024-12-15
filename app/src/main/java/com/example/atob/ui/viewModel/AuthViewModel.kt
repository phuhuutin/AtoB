package com.example.atob.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.util.copy
import com.example.atob.AtoBApplication
import com.example.atob.data.DefaultAppContainer
import com.example.atob.data.NetworkUserRepository
import com.example.atob.data.UserRepository
import com.example.atob.model.ChangePasswordRequest
import com.example.atob.model.InitialSetupDTO
import com.example.atob.model.LoginRequest
import com.example.atob.model.PayRateUpdateRequest
import com.example.atob.model.SignUpRequest
import com.example.atob.model.User
import com.example.atob.ui.state.AuthViewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp

class AuthViewModel(private var userRepository: UserRepository, private val application: AtoBApplication) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthViewUiState>(AuthViewUiState.Initial)
    val uiState: StateFlow<AuthViewUiState> get() = _uiState

//    suspend fun login(username: String, password: String) {
//    try{
//
//
//                _uiState.value = AuthViewUiState.Loading // Set to Loading state
//                val result = userRepository.login(LoginRequest(username, password))
//                if (result.isSuccess) {
//                    val user = result.getOrNull() as User
//                    _uiState.value = AuthViewUiState.Success(userInfo = user, dialogMessage = "Login successful!")
//                    application.container.updateNewAuthHeader()
//                    updateNewAuthHeader()
//                }else {
//                    _uiState.value = AuthViewUiState.Error(message = "Login failed: ${result.getOrNull()}")
//                }
//
//    }catch (e: Exception){
//        _uiState.value = AuthViewUiState.Error(message = "Login failed: ${e.message}")
//    }
//    }
    suspend fun loginSuspend(username: String, password: String) {

//            application.container.clearUserApiServiceAuthorHeader()
//            userRepository =  application.container.userRepository
            _uiState.value = AuthViewUiState.Loading // Set to Loading state
        try{
            val result = userRepository.login(LoginRequest(username, password))
            if (result.isSuccess) {
                val user = result.getOrNull() as User
                _uiState.value = AuthViewUiState.Success(userInfo = user, dialogMessage = "")
                application.container.updateNewAuthHeader()
                updateNewAuthHeader()
            }else {
                _uiState.value = AuthViewUiState.Error(message = "Login failed: username or password is incorrect")
            }
        }catch (exeption: Exception){
            _uiState.value = AuthViewUiState.Error(message = "Login failed: ${exeption.message}")
        }



    }
    suspend fun findUserByIdOrUsername(idOrString: String): User? {
        try{
            val result = if (idOrString.toLongOrNull() != null) {
                userRepository.getUserById(idOrString.toLong())
            } else {
                userRepository.getUserByUsername(idOrString)
            }
            // Check if the result was successful or failed
            return result.getOrElse {
                Log.e("NetworkUserRepository", "Error: ${it.message}")
                val currentUser = (_uiState.value as AuthViewUiState.Success).userInfo
                _uiState.value = AuthViewUiState.Success(userInfo = currentUser, dialogMessage = it.message ?: "Unknown error")
                null
            }
        }catch (e: Exception){
            Log.e("NetworkUserRepository", "Error: ${e.message}")
            val currentUser = (_uiState.value as AuthViewUiState.Success).userInfo
             _uiState.value = AuthViewUiState.Success(userInfo = currentUser, dialogMessage = e.message.toString() )

            return null
        }
    }

    private fun updateNewAuthHeader() {
         userRepository =  application.container.userRepository
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthViewUiState.Loading // Optional, loading state during logout (if needed)
            _uiState.value = AuthViewUiState.Success(userInfo = null, dialogMessage = "Logged out successfully.")
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest) {



                val result = userRepository.changePassword(request)
                if (result.isSuccess) {
                    _uiState.value = AuthViewUiState.Success(userInfo = result.getOrNull()!!, dialogMessage = "Password changed successfully!")
                    Log.d("ChangePassword", "new credentials in App: ${application.container.userRepository.getEncodedCredentials()}")
                    application.container.updateNewAuthHeader()
                } else {
                    val tempUserInfo = (_uiState.value as AuthViewUiState.Success).userInfo
                    _uiState.value = AuthViewUiState.Success(userInfo = tempUserInfo, dialogMessage = result.getOrNull() as String)
                }
//            } catch (e: Exception) {
//                _uiState.value = AuthViewUiState.Error(message = "Error: ${e.message}", dialogMessage = "An error occurred.")
//            }

    }

    fun clearDialogMessage() {
        _uiState.value = (_uiState.value as AuthViewUiState.Success).copy(dialogMessage = "")
    }


    fun signUp(request: SignUpRequest) {
        viewModelScope.launch {
            val result = userRepository.signUp(request)
            if (result.isSuccess) {
                val user = result.getOrNull() as User
                _uiState.value = (_uiState.value as AuthViewUiState.Success).copy(dialogMessage = "${user.username} is Signed up successfully!")
            } else {
                _uiState.value = (_uiState.value as AuthViewUiState.Success).copy(dialogMessage = "Sign up failed: ${result.exceptionOrNull()?.message}")
            }

        }

    }

    suspend fun setUpnewEmployer(request: InitialSetupDTO){
        try{
            val result = userRepository.setUpEmployer(request)
            result.onSuccess { user ->
                if(user is User)
                    _uiState.value = AuthViewUiState.Success(userInfo = user as User, dialogMessage = "Employer created successfully!")
                else{
                    _uiState.value = AuthViewUiState.Error(message = "Something went wrong")
                }
            }.onFailure { exception ->
                _uiState.value = AuthViewUiState.Error(message = exception.message ?: "Something went wrong")

            }
        }catch (e: Exception){
            _uiState.value = AuthViewUiState.Error(message = e.message ?: "Something went wrong")
        }
    }

    suspend fun deleteAttendanceRecord(id: Long): Boolean {
        try{
            val result = userRepository.deleteAttendanceRecord(id)
            result.onSuccess { message ->
                _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = message)
                return true;
            }.onFailure { exception ->
                _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = exception.message ?: "Something went wrong")
                return false;
            }
        }catch (e: Exception){
            _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = e.message ?: "Something went wrong")
            return false;
        }
        return false;
    }

    suspend fun updatePayRate(payRateUpdateRequest: PayRateUpdateRequest) {
        try{
            val result = userRepository.updatePayRate(payRateUpdateRequest)
            result.onSuccess { message ->
                _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = message)
        }.onFailure { exception ->
            _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = exception.message ?: "Something went wrong")
        }
    }catch (e: Exception){
            _uiState.value = AuthViewUiState.Success(userInfo = (_uiState.value as AuthViewUiState.Success).userInfo, dialogMessage = e.message ?: "Something went wrong")

        }
    }



    /**
     * Factory for [AuthViewModel] that takes [NetworkUserRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkUserRepository = application.container.userRepository
                AuthViewModel(networkUserRepository, application)
            }
        }
    }
}