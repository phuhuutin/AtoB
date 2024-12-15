package com.example.atob.ui.viewModel

import android.util.Log
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
import com.example.atob.model.UserShift
import com.example.atob.ui.state.HomeViewUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeViewModel(private var shiftRepository: ShiftRepository, private val application: AtoBApplication) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeViewUiState>(HomeViewUiState.Loading)
    val uiState: StateFlow<HomeViewUiState> = _uiState.asStateFlow()
    var employer: Employer? = null
    init {
      //  loadShifts()
    }

     fun updateNewAuthHeader() {
        shiftRepository =  application.container.shiftRepository
    }

    fun setDialMessage(message: String){
        val currentUiState = _uiState.value
        if (currentUiState is HomeViewUiState.Success) {
            _uiState.value = currentUiState.copy(dialogMessage = message)
        }
    }


    fun clockInOut(shiftId: Long){
        viewModelScope.launch {
            setDialMessage("Please wait...")
            val re: Result<String> = shiftRepository.addClockInOutRecord(shiftId)
            var message = ""
            if(re.isSuccess){
                message = re.getOrNull() ?: "No response from server"

                Log.e("NetworkShiftRepository", "clockInOut message: $message")
            } else{
                message = re.exceptionOrNull()?.message ?: "No response from server"
                Log.e("NetworkShiftRepository", "clockInOut message: $message")
            }

            val currentUiState = _uiState.value
            if (currentUiState is HomeViewUiState.Success) {
                _uiState.value = currentUiState.copy(dialogMessage = message)
            }

            if(re.isSuccess)
                    loadShiftsSuspend()
        }
    }

    fun clearDialogMessage(){
        val currentUiState = _uiState.value
        if(currentUiState is HomeViewUiState.Success)
            _uiState.value = currentUiState.copy(dialogMessage = "")
    }

    fun createShift(shift: ShiftDTO, userId: Long){
        viewModelScope.launch {
            shift.postedById = userId
            val re: Result<String> = shiftRepository.createShift(shift)
            var message = ""
            if(re.isSuccess){
                message = re.getOrNull() ?: "No response from server"
                Log.e("NetworkShiftRepository", "createShift message: $message")
            } else{
                message = re.exceptionOrNull()?.message ?: "No response from server"
                Log.e("NetworkShiftRepository", "createShift message: $message")
            }

        }
    }

    fun createShiftSetId(userId: Long): (ShiftDTO) -> Unit {
        return { shift -> createShift(shift, userId) }
    }

    fun loadShifts() {
        viewModelScope.launch {
            _uiState.value = HomeViewUiState.Loading // Set loading state

            shiftRepository.getAllShifts().collect { result ->
                if(result.isSuccess) {
                    val userShiftMapByDate = mutableMapOf<LocalDate, MutableList<UserShift>>()
                    var tempStartMonth = LocalDate.now()
                    var tempEndMonth =  LocalDate.now()
                    for (userShift in result.getOrThrow()) {
                        val date = userShift.date
                        // Add custom logic here, if needed
                        if (date.isAfter(tempEndMonth)) {
                            tempEndMonth = date
                        }

                        if(date.isBefore(tempStartMonth)){
                            tempStartMonth = date
                        }

                        // Add the UserShift to the corresponding date in the map
                        userShiftMapByDate.computeIfAbsent(date) { mutableListOf() }.add(userShift)
                    }
                    var dialogMessage = ""
                    if(_uiState.value is HomeViewUiState.Success){
                        val currentUiState = _uiState.value as HomeViewUiState.Success
                        dialogMessage = currentUiState.dialogMessage
                     }
                    _uiState.value = HomeViewUiState.Success(shifts = userShiftMapByDate, dialogMessage = dialogMessage, startMonth = tempStartMonth, endMonth = tempEndMonth) // Update shifts
                    Log.e("HomeViewModel", " ${tempStartMonth}-    #${tempEndMonth}")

                }else{
                    _uiState.value = HomeViewUiState.Error(errorMessage = result.exceptionOrNull()?.message ?: "Unknown error") // Set error message

                }
            }


        }
    }


    suspend fun loadShiftsSuspend() {

            shiftRepository.getAllShifts().collect { result ->
                if(result.isSuccess) {
                    //val userShiftMapByDate: Map<LocalDate, List<UserShift>> = result.getOrThrow().groupBy { it.date }
                    val userShiftMapByDate = mutableMapOf<LocalDate, MutableList<UserShift>>()
                    var tempStartMonth = (_uiState.value as HomeViewUiState.Success).startMonth
                    var tempEndMonth = (_uiState.value as HomeViewUiState.Success).endMonth
                    for (userShift in result.getOrThrow()) {
                        val date = userShift.date
                        // Add custom logic here, if needed
                        if (date.isAfter(tempEndMonth)) {
                            tempEndMonth = date
                        }

                        if(date.isBefore(tempStartMonth)){
                            tempStartMonth = date
                        }

                        // Add the UserShift to the corresponding date in the map
                        userShiftMapByDate.computeIfAbsent(date) { mutableListOf() }.add(userShift)
                    }
                    var dialogMessage = ""
                    if(_uiState.value is HomeViewUiState.Success){
                        val currentUiState = _uiState.value as HomeViewUiState.Success
                        dialogMessage = currentUiState.dialogMessage
                    }

                    _uiState.value = HomeViewUiState.Success(shifts = userShiftMapByDate, dialogMessage = dialogMessage, startMonth = tempStartMonth, endMonth = tempEndMonth) // Update shifts
                    Log.e("HomeViewModel", " ${tempStartMonth}-    #${tempEndMonth}")
                }else{
                    _uiState.value = HomeViewUiState.Error(errorMessage = result.exceptionOrNull()?.message ?: "Unknown error") // Set error message

                }
            }
    }

    fun addShift(shift: FindShift) {
        /**
         * TODO: inject user infor here
         */
        val addedShift: UserShift = shift.toUserShift("null")

        // Update _uiState with the new shifts map
        val currentUiState = _uiState.value
        if (currentUiState is HomeViewUiState.Success) {
            val updatedShifts = currentUiState.shifts.toMutableMap()
            val currentShiftsForDate = updatedShifts[addedShift.date] ?: emptyList()
            updatedShifts[addedShift.date] = currentShiftsForDate + addedShift

            _uiState.value = currentUiState.copy(shifts = updatedShifts) // Update shifts in success state
        }

    }

    fun dropShift(shift: FindShift) {
        val currentUiState = _uiState.value
        if (currentUiState is HomeViewUiState.Success) {
            val updatedShifts = currentUiState.shifts.toMutableMap()
            val currentShiftsForDate = updatedShifts[shift.date] ?: emptyList()
            val updatedShiftsForDate = currentShiftsForDate.filter { it.id != shift.id }
            updatedShifts[shift.date] = updatedShiftsForDate

            _uiState.value = currentUiState.copy(shifts = updatedShifts) // Update shifts in success state
        }

    }
    fun dropShift(shift: UserShift) {
        val currentUiState = _uiState.value
        if (currentUiState is HomeViewUiState.Success) {
            val updatedShifts = currentUiState.shifts.toMutableMap()
            val currentShiftsForDate = updatedShifts[shift.date] ?: emptyList()
            val updatedShiftsForDate = currentShiftsForDate.filter { it.id != shift.id }
            updatedShifts[shift.date] = updatedShiftsForDate

            _uiState.value = currentUiState.copy(shifts = updatedShifts) // Update shifts in success state
        }


    }

    fun updateEmployer(employer: Employer){
        this.employer = employer

    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AtoBApplication)
                val networkShiftRepository = application.container.shiftRepository
                HomeViewModel(networkShiftRepository, application)
            }
        }
    }
}
