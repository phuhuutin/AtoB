package com.example.atob.ui.state

import com.example.atob.model.Payroll

sealed class PayrollViewUiState {
    object Loading : PayrollViewUiState() // Represents the loading state

    data class Success(
        val payrolls: List<Payroll>, // List of Payroll objects
        var dialogMessage: String = "", // Optional dialog message
    ) : PayrollViewUiState()

    data class Error(val message: String) : PayrollViewUiState() // Represents the error state
}
