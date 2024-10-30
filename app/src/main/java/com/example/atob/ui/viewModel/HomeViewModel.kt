package com.example.atob.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.ShiftRepository
import com.example.atob.ui.state.HomeViewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

class HomeViewModel(private val shiftRepository: ShiftRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeViewUiState())
    val uiState: StateFlow<HomeViewUiState> = _uiState.asStateFlow()

    init {
        loadShifts()
    }

    private fun loadShifts() {
        viewModelScope.launch {
            _uiState.value = HomeViewUiState(isLoading = true) // Set loading state

            shiftRepository.getAllShifts().collect { result ->
                if(result.isSuccess) {
                    _uiState.value = HomeViewUiState(shifts = result.getOrThrow(), isLoading = false) // Update shifts

                }else{
                    _uiState.value = HomeViewUiState(isLoading = false, errorMessage = result.exceptionOrNull()?.message) // Set error message

                }
            }
        }
    }
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkShiftRepository = application.container.shiftRepository
                HomeViewModel(networkShiftRepository)
            }
        }
    }
}
