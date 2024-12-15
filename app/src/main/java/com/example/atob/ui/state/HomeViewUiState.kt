package com.example.atob.ui.state

import com.example.atob.model.Employer
import com.example.atob.model.UserShift
import java.time.LocalDate

//data class HomeViewUiState(
//    val shifts: Map<LocalDate, List<UserShift>> = mutableMapOf(),    // List of shifts
//    val shiftsToPick: List<UserShift> = emptyList(),    // List of shifts to pick
//    val isLoading: Boolean = false,            // Loading state
//    val errorMessage: String? = null,            // Error message if any
//    var selectedDate: LocalDate? = null        // Selected date
//)
sealed class HomeViewUiState {

    // Loading state, no data is needed
    object Loading : HomeViewUiState()

    // Content state with loaded shifts data and optional selected date
    data class Success(
        val shifts: Map<LocalDate, List<UserShift>> = mutableMapOf(),
        val shiftsToPick: List<UserShift> = emptyList(),
        val selectedDate: LocalDate? = null,
        var dialogMessage: String = "",
        var startMonth: LocalDate? = LocalDate.now(),
        var endMonth: LocalDate? = LocalDate.now()
    ) : HomeViewUiState()

    // Error state, with an error message
    data class Error(
        val errorMessage: String
    ) : HomeViewUiState()
}

