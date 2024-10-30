package com.example.atob.data

import com.example.atob.model.Shift
import com.example.atob.model.ShiftDTO
import com.example.atob.networks.ShiftApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

interface ShiftRepository {
    fun getAllShifts(): Flow<Result<List<Shift>>>
    fun getShiftById(id: Long): Flow<Result<Shift>>
    suspend fun createShift(shiftDTO: ShiftDTO): Result<String>
    suspend fun deleteShift(id: Long): Result<String>
    suspend fun addEmployeeToShift(shiftId: Long): Result<String>
}

class NetworkShiftRepository(
    private val shiftApiService: ShiftApiService
) : ShiftRepository {

    override fun getAllShifts(): Flow<Result<List<Shift>>> = flow {
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

    override fun getShiftById(id: Long): Flow<Result<Shift>> = flow {
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
                Result.success(response.body() ?: "Shift deleted successfully")
            } else {
                Result.failure(Exception("Failed to delete shift"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error"))
        }
    }

    override suspend fun addEmployeeToShift(shiftId: Long): Result<String> {
        return try {
            val response = shiftApiService.addEmployeeToShift(shiftId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Employee added to shift successfully")
            } else {
                Result.failure(Exception("Failed to add employee to shift"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error"))
        }
    }
}