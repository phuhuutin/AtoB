package com.example.atob.networks

import com.example.atob.model.ChangePasswordRequest
import com.example.atob.model.ClockInOutRecord
import com.example.atob.model.ClockInOutRecordsUpdate
import com.example.atob.model.InitialSetupDTO
import com.example.atob.model.LoginRequest
import com.example.atob.model.PayRateUpdateRequest
import com.example.atob.model.Payroll
import com.example.atob.model.Report
import com.example.atob.model.ReportDTO
import com.example.atob.model.SignUpRequest
import com.example.atob.model.User
import retrofit2.Response
import retrofit2.http.*

interface UserApiService  {
    @GET("api/user")
    suspend fun getAllUsers(): List<User>

    @GET("api/user/id/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @GET("api/user/username/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<User>

    @GET("api/user/payrolls")
    suspend fun getPayrolls( ): Response<List<Payroll>>

    @POST("api/user")
    suspend fun createUser(@Body user: User): User

    @POST("api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>



    @DELETE("api/user/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @POST("api/user/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<User>

    @PUT("api/user/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<User>


    @GET("api/reports")
    suspend fun getAllReports(): Response<List<Report>>

    @POST("api/reports")
    suspend fun createReportForUser(
        @Body report: ReportDTO
    ): Response<Report>

    @GET("api/reports/closed/{id}")
    suspend fun closeReportById(@Path("id") id: Long): Response<Report>

    @PUT("api/clock")
    suspend fun updateClock(@Body clock: ClockInOutRecordsUpdate): Response<ClockInOutRecord>

    @POST("api/user/setup")
    suspend fun setUpEmployer(@Body data: InitialSetupDTO): Response<User>

    @DELETE("api/employer/deleteRecord/{id}")
    suspend fun deleteAttendanceRecord(@Path("id") id: Long): Response<String>

    @PUT("api/user/payrate")
    suspend fun updatePayRate(@Body payRateUpdateRequest: PayRateUpdateRequest): Response<String>



}
//interface UserService {
//    suspend fun createUser(user: User): User
//    suspend fun getAllUsers(): List<User>
//    // Other user-related business logic
//}
//
//
//class UserServiceImpl(private val userRepository: UserRepository) : UserService {
//    override suspend fun createUser(user: User): User {
//        // Business logic for creating a user
//        return userRepository.saveUser(user)
//    }
//
//    override suspend fun getAllUsers(): List<User> {
//        // Business logic for fetching all users
//        return userRepository.getAllUsers()
//    }
//}