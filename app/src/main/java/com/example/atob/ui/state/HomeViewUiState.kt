package com.example.atob.ui.state

import com.example.atob.model.Shift
import kotlinx.coroutines.flow.Flow

data class HomeViewUiState(
    val shifts: List<Shift> = emptyList(),    // List of shifts
    val isLoading: Boolean = false,            // Loading state
    val errorMessage: String? = null            // Error message if any
)
