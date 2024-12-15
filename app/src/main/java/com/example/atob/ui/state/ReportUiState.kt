package com.example.atob.ui.state

import com.example.atob.model.Report

sealed class ReportUiState {
    object Loading : ReportUiState() // Represents the loading state
    data class Success(val reports: List<Report>, val message: String) : ReportUiState() // Successfully fetched reports
    data class Error(val message: String) : ReportUiState() // Error message
}