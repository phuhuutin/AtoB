package com.example.atob.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.UserRepository
import com.example.atob.model.ClockInOutRecordsUpdate
import com.example.atob.model.Report
import com.example.atob.model.ReportDTO
import com.example.atob.model.User
import com.example.atob.ui.state.HomeViewUiState
import com.example.atob.ui.state.ReportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel(
        private var userRepository: UserRepository,
        private val application: AtoBApplication
    ) : ViewModel() {
    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    // Fetch all reports, only allowed for users with MANAGER role
    fun getAllReports(user: User?) {
        Log.d("getAllReports", "Function called with user: $user")
        if (user?.role == "MANAGER") {
            Log.d("getAllReports", "User is a MANAGER. Fetching all reports...")
            _uiState.value = ReportUiState.Loading
            Log.d("getAllReports", "State set to Loading")

            viewModelScope.launch {
                try {
                    val result = userRepository.getAllReports()
                    result.onSuccess { reports ->
                        Log.d("getAllReports", "Successfully fetched reports: $reports")
                        _uiState.value = ReportUiState.Success(reports, "")
                        Log.d("getAllReports", "State set to Success")
                    }
                    result.onFailure { exception ->
                        Log.e("getAllReports", "Failed to fetch reports: ${exception.message}", exception)
                        _uiState.value = ReportUiState.Error("Failed to fetch reports")
                        Log.d("getAllReports", "State set to Error")
                    }
                } catch (e: Exception) {
                    Log.e("getAllReports", "Unexpected exception occurred: ${e.message}", e)
                    _uiState.value = ReportUiState.Error("Unexpected error occurred")
                    Log.d("getAllReports", "State set to Error due to exception")
                }
            }
        } else {
            Log.d("getAllReports", "User is not a MANAGER. Calling FindReportByUserId...")
            // Call FindReportByUserId logic here
        }
    }

    fun updateNewAuthHeader() {
        userRepository =  application.container.userRepository
    }

    // Create a report, only allowed for users with MANAGER role
    fun createReport( report: ReportDTO) {
        val updatedReports = (uiState.value as? ReportUiState.Success)?.reports?.toMutableList()
        _uiState.value = ReportUiState.Loading
        viewModelScope.launch {
            try {
                val result = userRepository.createReportForUser(report)
                result.onSuccess { newReport ->
                    if (newReport is Report) {
                        updatedReports?.add(newReport)
                        updatedReports?.let {
                            _uiState.value = ReportUiState.Success(it, "Report created successfully")
                        }
                    } else {
                        _uiState.value = ReportUiState.Error("Unexpected response type")
                    }
                }.onFailure { exception ->
                    _uiState.value = ReportUiState.Error(exception.message ?: "An unknown error occurred")
                }
            } catch (e: Exception) {
                // Catch and handle any unexpected exceptions
                _uiState.value = ReportUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }

    }
    fun updateClock(clock: ClockInOutRecordsUpdate, reportId: Long) {
        val updatedReports = (uiState.value as? ReportUiState.Success)?.reports?.toMutableList()
        _uiState.value = ReportUiState.Loading

        viewModelScope.launch {
            val result = userRepository.updateClock(clock)
            result.onSuccess {

                val closedResult = userRepository.closeReportById(reportId)
                closedResult.onSuccess {
                    updatedReports?.forEach { report ->
                        if (report.id == reportId) {
                            report.completed = true
                        }
                    }
                }.onFailure { exception ->
                    _uiState.value = ReportUiState.Success( updatedReports ?: emptyList(),exception.message ?: "An unknown error occurred")
                }


                _uiState.value = ReportUiState.Success(
                    updatedReports ?: emptyList(),
                    "The clock was updated, and the report was successfully closed"
                )


            } .onFailure { exception ->
                _uiState.value = ReportUiState.Success( updatedReports ?: emptyList(),exception.message ?: "An unknown error occurred")
            }

        }
    }

    fun closeReport(reportId: Long) {
        val updatedReports = (uiState.value as? ReportUiState.Success)?.reports?.toMutableList()
        _uiState.value = ReportUiState.Loading

        viewModelScope.launch {
            val result = userRepository.closeReportById(reportId)
            result.onSuccess {
                updatedReports?.forEach { report ->
                    if (report.id == reportId) {
                        report.completed = true
                    }
                }
                _uiState.value = ReportUiState.Success(
                    updatedReports ?: emptyList(),
                    "The report is closed successfully"
                )
            }.onFailure { exception ->
                _uiState.value = ReportUiState.Success( updatedReports ?: emptyList(),exception.message ?: "An unknown error occurred")
            }

        }

    }

    fun clearMessage() {
        val currentUiState = _uiState.value
        if(currentUiState is ReportUiState.Success)
            _uiState.value = currentUiState.copy(message = "")
        else if(currentUiState is ReportUiState.Error)
            _uiState.value = currentUiState.copy(message = "")

    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkUserRepository = application.container.userRepository
                ReportViewModel(networkUserRepository, application)
            }
        }
    }




}