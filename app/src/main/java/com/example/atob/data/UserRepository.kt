package com.example.atob.data

import com.example.atob.model.LoginRequest
import com.example.atob.model.SignUpRequest
import com.example.atob.model.User
import com.example.atob.networks.UserApiService
import kotlin.math.log

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User
    suspend fun createUser(user: User): User
    suspend fun deleteUser(id: Long)
    suspend fun signUp(signUpRequest: SignUpRequest): String
    suspend fun saveUser(user: User): User
    suspend fun login(loginRequest: LoginRequest): User
}

class NetworkUserRepository(
    private val userApiService: UserApiService
) : UserRepository {
    override suspend fun saveUser(user: User): User {
        // Network call to save a user
        return userApiService.createUser(user)
    }

    override suspend fun login(loginRequest: LoginRequest): User {
        return userApiService.login(loginRequest);
    }

    override suspend fun getAllUsers(): List<User> = userApiService.getAllUsers()
    override suspend fun getUserById(id: Long): User = userApiService.getUserById(id)
    override suspend fun createUser(user: User): User = userApiService.createUser(user)
    override suspend fun deleteUser(id: Long) = userApiService.deleteUser(id)
    override suspend fun signUp(signUpRequest: SignUpRequest): String = userApiService.signUp(signUpRequest)
}