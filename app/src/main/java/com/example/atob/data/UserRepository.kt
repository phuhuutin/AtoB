package com.example.atob.data

import android.util.Log
import com.example.atob.model.ChangePasswordRequest
import com.example.atob.model.ClockInOutRecordsUpdate
import com.example.atob.model.InitialSetupDTO
import com.example.atob.model.LoginRequest
import com.example.atob.model.PayRateUpdateRequest
import com.example.atob.model.Payroll
import com.example.atob.model.Report
import com.example.atob.model.ReportDTO
import com.example.atob.model.SignUpRequest
import com.example.atob.model.User
import com.example.atob.networks.UserApiService
import java.util.Base64

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): Result<User>
    suspend fun getUserByUsername(username: String): Result<User>
    suspend fun getPayrolls(): Result<List<Payroll>>
    suspend fun createUser(user: User): User
    suspend fun deleteUser(id: Long)
    suspend fun signUp(signUpRequest: SignUpRequest): Result<User>
    suspend fun saveUser(user: User): User
    suspend fun login(loginRequest: LoginRequest): Result<User>
    abstract fun getEncodedCredentials(): String
    suspend fun changePassword(request: ChangePasswordRequest): Result<User>

    suspend fun getAllReports(): Result<List<Report>>
    suspend fun createReportForUser(report: ReportDTO): Result<Any>
    suspend fun closeReportById(id: Long): Result<Any>
    suspend fun updateClock(clock: ClockInOutRecordsUpdate): Result<Any>
    suspend fun setUpEmployer(initialSetupDTO: InitialSetupDTO): Result<Any>
    suspend fun deleteAttendanceRecord(id: Long): Result<String>
    suspend fun updatePayRate(payRateUpdateRequest: PayRateUpdateRequest): Result<String>
}

class NetworkUserRepository(
    private val userApiService: UserApiService
) : UserRepository {

    private var encodedCredentials: String? = null


    override suspend fun saveUser(user: User): User {
        // Network call to save a user
        return userApiService.createUser(user)
    }

    override suspend fun login(loginRequest: LoginRequest): Result<User> {



        val response = userApiService.login(loginRequest);
        Log.d("NetworkUserRepository", "Login response: $response")
        if (response.isSuccessful) {
            encodedCredentials = Base64.getEncoder()
                .encodeToString("${loginRequest.username}:${loginRequest.password}".toByteArray())
            val user = response.body() as User
            return Result.success(user)
        } else {
            val errorBody = response.errorBody()?.string() as String
            Log.d("NetworkUserRepository", "Login error: $errorBody")
            return Result.failure(Exception(errorBody))
        }

    }

    override fun getEncodedCredentials(): String {
        return encodedCredentials ?: ""
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Result<User> {
        Log.d("ChangePassword", "old credentials: $encodedCredentials")
        return try {
            val response = userApiService.changePassword(request)
            if(response.isSuccessful){
                encodedCredentials = Base64.getEncoder()
                    .encodeToString("${request.username}:${request.newPassword}".toByteArray())
            }
            Log.d("ChangePassword", "new credentials: $encodedCredentials")

            Result.success(response.body() as User)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllReports(): Result<List<Report>> {
        return try {
            val response = userApiService.getAllReports()
            if (response.isSuccessful) {
                // If the response is successful, return the list of reports
                Result.success(response.body() ?: emptyList())
            } else {
                // Return an error if the response is not successful
                Result.failure(Exception("Failed to fetch reports"))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun createReportForUser(report: ReportDTO): Result<Any> {
        return try {
            val response = userApiService.createReportForUser(report)
            if (response.isSuccessful) {
                // If the response is successful, return the created report
                Result.success(response.body() ?: throw Exception("Null response"))
            } else {
                // Extract the error message from the response
                val errorMessage = response.errorBody()?.string() ?: "Failed to create report"
                Log.d("ReportScreen", "Error message: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun setUpEmployer(initialSetupDTO: InitialSetupDTO): Result<Any> {
        return try{
            val response = userApiService.setUpEmployer(initialSetupDTO)
            if(response.isSuccessful){
                Result.success(response.body() ?: throw Exception("Null response"))
            }else {
                // Extract the error message from the response
                val errorMessage = response.errorBody()?.string() ?: "Failed to create report"
                Log.d("ReportScreen", "Error message: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
            } catch (e: Exception) {
                // Catch any errors during the network request and return a failure
                Result.failure(e)
        }
    }

    override suspend fun deleteAttendanceRecord(id: Long): Result<String> {
        return try {
            val response = userApiService.deleteAttendanceRecord(id)
            if (response.isSuccessful) {
                // If the response is successful, return the created report
                Result.success(response.body() ?: throw Exception("Null response"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to delete record"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun updatePayRate(payRateUpdateRequest: PayRateUpdateRequest): Result<String> {
        return try {
            val response = userApiService.updatePayRate(payRateUpdateRequest)
            if (response.isSuccessful) {
                // If the response is successful, return the created report
                Result.success(response.body() ?: throw Exception("Null response"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to update pay rate"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun closeReportById(id: Long): Result<Any> {
        return try {
            val response = userApiService.closeReportById(id)
            if (response.isSuccessful) {
                // If the response is successful, return the created report
                Result.success(response.body() ?: throw Exception("Null response"))
                }
            else {
                // Extract the error message from the response
                val errorMessage = response.errorBody()?.string() ?: "Failed to close report"
                Log.d("ReportScreen", "Error message: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun updateClock(clock: ClockInOutRecordsUpdate): Result<Any> {
        return try{
            val response = userApiService.updateClock(clock)
            if(response.isSuccessful){
                Result.success(response.body() ?: throw Exception("Null response"))
            }else {
                // Extract the error message from the response
                val errorMessage = response.errorBody()?.string() ?: "Failed to update clock"
                Log.d("ReportScreen", "Error message: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Catch any errors during the network request and return a failure
            Result.failure(e)
        }
    }

    override suspend fun getAllUsers(): List<User> = userApiService.getAllUsers()
    override suspend fun getUserById(id: Long): Result<User> {
        return try{
            val response = userApiService.getUserById(id)
            if(response.isSuccessful){
                Result.success(response.body() as User)
            }else if(response.code() == 404){
                Log.e("NetworkUserRepository", "No user is found!")
                Result.failure(Exception("No user is found!"))
            }else {
                throw Exception("Failed to fetch user")
            }
        }catch(e: Exception){
            Result.failure(e)
        }

    }

    override suspend fun getUserByUsername(username: String): Result<User> {
        return try {
            val response = userApiService.getUserByUsername(username)
            if(response.isSuccessful){
                Result.success(response.body() as User)
            }else if(response.code() == 404){
                Result.failure(Exception("No user is found!"))
            }else {
                throw Exception("Failed to fetch user")
            }
        }catch(e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getPayrolls(): Result<List<Payroll>> {
        return try{
            val response = userApiService.getPayrolls()
            Log.d("NetworkUserRepository", "Payrolls response: $response")
            if(response.isSuccessful){
                val payrolls = response.body() as List<Payroll>
                Result.success(payrolls)
            }else {
                Result.failure(Exception("Failed to fetch payrolls"))
            }
        }catch(e: Exception){
            Result.failure(e)
        }
    }


    override suspend fun createUser(user: User): User = userApiService.createUser(user)
    override suspend fun deleteUser(id: Long) = userApiService.deleteUser(id)
    override suspend fun signUp(signUpRequest: SignUpRequest): Result<User> {
        return try {
            val response = userApiService.signUp(signUpRequest)
            if (response.isSuccessful) {
                Result.success(response.body() as User)
            }else{
                Result.failure(Exception("Failed to sign up"))
            }
         } catch (e: Exception) {
            Result.failure(e)
        }

    }
}