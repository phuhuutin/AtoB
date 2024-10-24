package com.example.atob.networks

import com.example.atob.data.UserRepository
import com.example.atob.model.LoginRequest
import com.example.atob.model.SignUpRequest
import com.example.atob.model.User
import retrofit2.http.*
import kotlinx.coroutines.Deferred

interface UserApiService  {
    @GET("api/user")
    suspend fun getAllUsers(): List<User>

    @GET("api/user/{id}")
    suspend fun getUserById(@Path("id") id: Long): User

    @GET("api/user/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): User

    @POST("api/user")
    suspend fun createUser(@Body user: User): User

    @POST("api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): User

    @DELETE("api/user/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @POST("api/user/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): String
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