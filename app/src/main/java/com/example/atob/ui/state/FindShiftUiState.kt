package com.example.atob.ui.state

import com.example.atob.model.Employer
import com.example.atob.model.FindShift
import com.example.atob.model.UserShift

sealed class FindShiftUiState {
    object Loading : FindShiftUiState()
    data class Success(
        val userShifts: Set<FindShift>,
        var showDialog: Boolean = false,
        var dialogMessage: String = ""
    ) : FindShiftUiState()
    data class Error(val message: String) : FindShiftUiState()


}