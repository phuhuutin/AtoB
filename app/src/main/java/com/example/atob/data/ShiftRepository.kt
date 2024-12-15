package com.example.atob.data

import android.util.Log
import com.example.atob.model.Employer
import com.example.atob.model.FindShift
import com.example.atob.model.UserShift
import com.example.atob.model.ShiftDTO
import com.example.atob.networks.ShiftApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

interface ShiftRepository {
    fun getAllShifts(): Flow<Result<List<UserShift>>>
    fun getShiftById(id: Long): Flow<Result<UserShift>>
    suspend fun getShiftsAfterNow(employerId: Long): Result<Set<FindShift>>
    suspend fun createShift(shiftDTO: ShiftDTO): Result<String>
    suspend fun deleteShift(id: Long): Result<String>
    suspend fun addEmployeeToShift(shiftId: Long): Result<String>
    suspend fun dropShfit(shiftId: Long): Result<String>
    suspend fun addClockInOutRecord(shiftId: Long): Result<String>

}

class NetworkShiftRepository(
    private val shiftApiService: ShiftApiService
) : ShiftRepository {

    override fun getAllShifts(): Flow<Result<List<UserShift>>> = flow {
        try {
            val response = shiftApiService.getAllShifts()
            if (response.isSuccessful) {
                response.body()?.let { shifts ->
                    emit(Result.success(shifts))
                } ?: emit(Result.failure(Exception("No shifts found")))
            } else {
                emit(Result.failure(Exception("Failed to fetch shifts")))
            }
        } catch (e: IOException) {
            emit(Result.failure(Exception("Network error")))
        } catch (e: HttpException) {
            emit(Result.failure(Exception("Server error")))
        }
    }

    override fun getShiftById(id: Long): Flow<Result<UserShift>> = flow {
        try {
            val response = shiftApiService.getShiftById(id)
            if (response.isSuccessful) {
                response.body()?.let { shift ->
                    emit(Result.success(shift))
                } ?: emit(Result.failure(Exception("Shift not found")))
            } else {
                emit(Result.failure(Exception("Failed to fetch shift")))
            }
        } catch (e: IOException) {
            emit(Result.failure(Exception("Network error")))
        } catch (e: HttpException) {
            emit(Result.failure(Exception("Server error")))
        }
    }

    override suspend fun getShiftsAfterNow(employerId : Long): Result<Set<FindShift>> {
        Log.d("NetworkShiftRepository", "Fetching shifts after now...")
        val response = shiftApiService.getShiftsAfterNow(employerId)
        return if (response.isSuccessful) {
            response.body()?.let { items ->
                Result.success(items)
            } ?: Result.failure(Exception("Response body is null"))
        } else {
            Result.failure(
                Exception("Failed to fetch items: ${response.errorBody()?.string()}")
            )
        }
    }


//            Log.d("NetworkShiftRepository", "Fetching shifts after now...")
//            val response = shiftApiService.getShiftsAfterNow()
//            // Check if the response is successful
//            if (response.isSuccessful) {
//                response.body()?.let { items ->
//                    emit(Result.success(items))
//                } ?: emit(Result.failure(Exception("Response body is null")))
//            } else {
//                // Handle the error case with a custom exception
//                emit(
//                    Result.failure(
//                        Exception(
//                            "Failed to fetch items: ${
//                                response.errorBody()?.string()
//                            }"
//                        )
//                    )
//                )
//            }




    override suspend fun createShift(shiftDTO: ShiftDTO): Result<String> {
        return try {
            val response = shiftApiService.createShift(shiftDTO)
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Shift created successfully")
            } else {
                Result.failure(Exception("Failed to create shift"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error"))
        }
    }

    override suspend fun deleteShift(id: Long): Result<String> {
        return try {
            val response = shiftApiService.deleteShift(id)
            if (response.isSuccessful) {
                // Use the message from the server response or provide a fallback
                Result.success(response.body() ?: "Shift deleted successfully")
            } else {
                // Handle specific HTTP errors based on server logic
                val errorMessage = when (response.code()) {
                    401 -> "Unauthorized: Only managers can delete shifts"
                    403 -> "Forbidden: You do not have permission to delete this shift"
                    400 -> "Invalid request: ${response.errorBody()?.string() ?: "Bad Request"}"
                    else -> "Failed to delete shift: ${response.errorBody()?.string() ?: "Server error"}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    override suspend fun addEmployeeToShift(shiftId: Long): Result<String> {
        return try {
            val response = shiftApiService.addEmployeeToShift(shiftId)
            when {
                response.isSuccessful -> Result.success(response.body() ?: "Employee added to shift successfully")
                response.code() == 400 -> Result.failure(Exception("Bad Request: Failed to add employee to shift"))
                response.code() == 500 -> Result.failure(Exception("Server Error: Please try again later"))
                else -> Result.failure(Exception("Unexpected error occurred"))
            }
        } catch (e: IOException) {
            Result.failure(IOException("Network error"))
        } catch (e: HttpException) {
            Result.failure(HttpException(e.response()))
        }
    }

    override suspend fun dropShfit(shiftId: Long): Result<String> {
        return try {
            val response = shiftApiService.dropShift(shiftId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Shift dropped successfully")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to drop shift"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error"))
        }
    }

    override suspend fun addClockInOutRecord(shiftId: Long): Result<String> {
        return try {
            val response: Response<String> = shiftApiService.addClockInOutRecord(shiftId)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Result.success(responseBody ?: "Clock in/out record added successfully null")
            } else if(response.code() == 400) {
                val message = response.errorBody()?.string() ?: "400 Bad Request"
                Result.failure(Exception(message))
            } else {
                Log.e("NetworkShiftRepository", "Failed to add clock in/out record: ${response.message()}")
                Result.failure(Exception("Failed to add clock in/out record"))
            }
        } catch (e: Exception) {
            Log.e("NetworkShiftRepository", "Exception: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}