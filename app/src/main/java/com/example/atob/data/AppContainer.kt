package com.example.atob.data

import com.example.atob.networks.ShiftApiService
import com.example.atob.networks.UserApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val userRepository: UserRepository
    val shiftRepository: ShiftRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    private val shiftApiService: ShiftApiService by lazy {
        retrofit.create(ShiftApiService::class.java)
    }

    override val userRepository: UserRepository by lazy {
        NetworkUserRepository(userApiService)
    }
    override val shiftRepository: ShiftRepository by lazy {
        NetworkShiftRepository(shiftApiService)
    }

}