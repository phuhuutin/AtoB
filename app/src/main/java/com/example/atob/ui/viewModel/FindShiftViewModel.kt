package com.example.atob.ui.viewModel

import android.util.Log
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atob.AtoBApplication
import com.example.atob.data.ShiftRepository
import com.example.atob.model.Employer
import com.example.atob.model.FindShift
import com.example.atob.model.ShiftDTO
import com.example.atob.ui.state.FindShiftUiState
import com.example.atob.ui.state.HomeViewUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindShiftViewModel(private var shiftRepository: ShiftRepository, private val application: AtoBApplication) : ViewModel() {
    // Private MutableStateFlow for internal updates
    private val _uiState = MutableStateFlow<FindShiftUiState>(FindShiftUiState.Loading)
    var employer: Employer? = null
    // Expose an immutable StateFlow to the UI layer
    val uiState: StateFlow<FindShiftUiState> = _uiState.asStateFlow()
    // This job will track the collecting job
    private var shiftsJob: Job? = null

    fun getShiftsAfterNow() {
        // Launch a coroutine to collect itemsFlow
         shiftsJob = viewModelScope.launch {
            refreshShifts()
        }
    }

      fun updateNewAuthHeader() {
        shiftRepository =  application.container.shiftRepository
    }


    suspend fun refreshShiftsWithMessage(message: String) {
        shiftRepository.getShiftsAfterNow(employer!!.id).let{ result ->
            _uiState.value =
                if(result.isSuccess)
                // Use safe call to avoid non-null assertion

                    result.getOrNull()?.let { shifts ->
                        FindShiftUiState.Success(
                            userShifts = result.getOrThrow(),
                            dialogMessage  = message
                        )
                    } ?: FindShiftUiState.Error("No shifts available.")
                else
                    FindShiftUiState.Error((result.exceptionOrNull()?.message ?: "Unknown error"))
        }
    }

    suspend fun refreshShifts() {
        shiftRepository.getShiftsAfterNow(employer!!.id).let{ result ->
            _uiState.value =
                if(result.isSuccess)
                // Use safe call to avoid non-null assertion

                    result.getOrNull()?.let { shifts ->
                        FindShiftUiState.Success(
                            userShifts = result.getOrThrow(),
                            dialogMessage  =  (_uiState.value as? FindShiftUiState.Success)?.dialogMessage ?: ""
                        )
                    } ?: FindShiftUiState.Error("No shifts available.")
                else
                    FindShiftUiState.Error((result.exceptionOrNull()?.message ?: "Unknown error"))
        }
    }
    fun createShift(shift: ShiftDTO, userId: Long, employerId: Long){
        viewModelScope.launch {
            setDialogMessage("Creating shift...")
            shift.postedById = userId
            shift.employerId = employerId
            val re: Result<String> = shiftRepository.createShift(shift)
            var message = ""
            if(re.isSuccess){
                message = re.getOrNull() ?: "No response from server"
                Log.e("NetworkShiftRepository", "createShift message: $message")
            } else{
                message = re.exceptionOrNull()?.message ?: "No response from server"
                Log.e("NetworkShiftRepository", "createShift message: $message")
            }
            refreshShiftsWithMessage(message)
        }
    }
    fun deleteShift(shiftId: Long, reloadShifts: suspend ()->Unit){
        viewModelScope.launch {
            setDialogMessage("Deleting shift...")
            val re: Result<String> = shiftRepository.deleteShift(shiftId)
            var message = ""
            if(re.isSuccess){
                reloadShifts()
                message = re.getOrNull() ?: "No response from server"
                Log.e("NetworkShiftRepository", "deleteShift message: $message")
            } else{
                message = re.exceptionOrNull()?.message ?: "No response from server"
                Log.e("NetworkShiftRepository", "deleteShift message: $message")
            }
            refreshShiftsWithMessage(message)

        }
    }

    fun createShiftSetId(userId: Long, employerId: Long): (ShiftDTO) -> Unit {
        return { shift -> createShift(shift, userId, employerId) }
    }

    fun clearDialogMessage() {
        val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
        _uiState.value = FindShiftUiState.Success(
            userShifts = currentShifts,
            showDialog = false,
            dialogMessage = ""
        )
    }

    fun updateEmployer(employer: Employer){
        this.employer = employer
    }

//    fun addShift(shift: FindShift){
//        viewModelScope.launch {
//            try{
//                val re : Result<String> = shiftRepository.addEmployeeToShift(shift.id)
//                var message = ""
//                message = if(re.isSuccess){
//                    re.getOrThrow()
//                } else {
//                    re.exceptionOrNull()?.message ?: "Unknown error"
//                }
//                val currentShifts = shiftRepository.getShiftsAfterNow().getOrThrow()
//                _uiState.value = FindShiftUiState.Success(
//                    userShifts = currentShifts,
//                    showDialog = true,
//                    dialogMessage = message
//                )
//               // getShiftsAfterNow()
//                Log.d("FindShiftScreen", message)
//              //  Log.d("FindShiftScreen", (_uiState.value as FindShiftUiState.Success).dialogMessage)
//
//            } catch (e: Exception){
//                // Handle exception, show dialog with the error message
//                val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
//                _uiState.value = FindShiftUiState.Success(
//                    userShifts = currentShifts,
//                    showDialog = true,
//                    dialogMessage = e.message ?: "An error occurred"
//                )
//            }
//
//        }
//    }

    fun setDialogMessage(message: String){
        val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
        _uiState.value = FindShiftUiState.Success(
            userShifts = currentShifts,
            showDialog = true,
            dialogMessage = message
        )
    }

    suspend fun addShiftSuspend(shift: FindShift): Boolean{

            try{
                setDialogMessage("Adding shift...")
                val re : Result<String> = shiftRepository.addEmployeeToShift(shift.id)
                var message = ""
                if(re.isSuccess){
                    val currentShifts = shiftRepository.getShiftsAfterNow(employer!!.id).getOrThrow()
                    message = re.getOrThrow()
                    _uiState.value = FindShiftUiState.Success(
                        userShifts = currentShifts,
                        showDialog = true,
                        dialogMessage = message
                    )
                    return true;
                } else {
                    val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
                    message = re.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = FindShiftUiState.Success(
                        userShifts = currentShifts,
                        showDialog = true,
                        dialogMessage = message
                    )
                    return false
                }
            } catch (e: Exception){
                // Handle exception, show dialog with the error message
                val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
                _uiState.value = FindShiftUiState.Success(
                    userShifts = currentShifts,
                    showDialog = true,
                    dialogMessage = e.message ?: "An error occurred"
                )
                return false
            }


    }

    fun dropShift(shiftId: Long) {
        viewModelScope.launch {
            try{
                val re : Result<String> = shiftRepository.dropShfit(shiftId)
                var message = ""
                message = if(re.isSuccess){
                    re.getOrThrow()
                } else {
                    re.exceptionOrNull()?.message ?: "Unknown error"
                }
                val currentShifts = shiftRepository.getShiftsAfterNow(employer!!.id).getOrThrow()
                _uiState.value = FindShiftUiState.Success(
                    userShifts = currentShifts,
                    showDialog = true,
                    dialogMessage = message
                )

            } catch (e: Exception){
                val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
                _uiState.value = FindShiftUiState.Success(
                    userShifts = currentShifts,
                    showDialog = true,
                    dialogMessage = e.message ?: "An error occurred"
                )
            }

        }
    }

    suspend fun dropShiftSuspend(shiftId: Long): Boolean {

            try{
                setDialogMessage("Dropping shift...")
                val re : Result<String> = shiftRepository.dropShfit(shiftId)
                var message = ""
                if(re.isSuccess){
                    val currentShifts = shiftRepository.getShiftsAfterNow(employer!!.id).getOrThrow()
                    message = re.getOrThrow()
                    _uiState.value = FindShiftUiState.Success(
                        userShifts = currentShifts,
                        showDialog = true,
                        dialogMessage = message
                    )
                    return true;
                } else {
                    val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
                    message = re.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = FindShiftUiState.Success(
                        userShifts = currentShifts,
                        showDialog = true,
                        dialogMessage = message
                    )
                    return false
                }

            } catch (e: Exception){
                val currentShifts = (_uiState.value as? FindShiftUiState.Success)?.userShifts ?: emptySet()
                _uiState.value = FindShiftUiState.Success(
                    userShifts = currentShifts,
                    showDialog = true,
                    dialogMessage = e.message ?: "An error occurred"
                )
                return false
            }


    }


    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkShiftRepository = application.container.shiftRepository
                FindShiftViewModel(networkShiftRepository, application)
            }
        }
    }

}