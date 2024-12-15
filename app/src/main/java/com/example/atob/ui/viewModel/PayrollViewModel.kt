package com.example.atob.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.UserRepository
import com.example.atob.ui.state.PayrollViewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PayrollViewModel(
    private var userRepository: UserRepository,
    private val application: AtoBApplication
) : ViewModel() {

    // StateFlow for UI state
    private val _uiState = MutableStateFlow<PayrollViewUiState>(PayrollViewUiState.Loading)
    val uiState: StateFlow<PayrollViewUiState> get() = _uiState
    var payRate: Double = 0.0
    init {
      //  fetchPayrolls()
    }

    fun updatePayRate(payRate: Double) {
        this.payRate = payRate
    }

    fun fetchPayrolls() {
        viewModelScope.launch {
            _uiState.value = PayrollViewUiState.Loading
            try {
                val result = userRepository.getPayrolls() // Assuming this returns a list of Payroll
                val payrolls = result.getOrThrow()
                _uiState.value = PayrollViewUiState.Success(payrolls = payrolls)
            } catch (e: Exception) {
                _uiState.value = PayrollViewUiState.Error(
                    message = e.localizedMessage ?: "An unknown error occurred"
                )
            }
        }
    }

    fun clearDialogMessage() {
        if (_uiState.value is PayrollViewUiState.Success) {
            val currentUiState = _uiState.value as PayrollViewUiState.Success
            _uiState.value = currentUiState.copy(dialogMessage = "")
        }
    }


    fun  updateNewAuthHeader() {
        userRepository =  application.container.userRepository
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkUserRepository = application.container.userRepository
                PayrollViewModel(networkUserRepository, application)
            }
        }
    }
}