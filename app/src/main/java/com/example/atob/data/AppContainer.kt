package com.example.atob.data

import android.util.Log
import androidx.privacysandbox.tools.core.model.Type
import com.example.atob.BuildConfig
import com.example.atob.R
import com.example.atob.interceptor.AuthInterceptor
import com.example.atob.networks.ShiftApiService
import com.example.atob.networks.UserApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory



interface AppContainer {
    var userRepository: UserRepository
    var shiftRepository: ShiftRepository // this one needs Authorization Basic header, which is abtain from userRepository.login()
    fun setUserApiServiceToAuthenticated()
    fun clearUserApiServiceAuthorHeader()
    fun updateNewAuthHeader()
}

class DefaultAppContainer : AppContainer {


    private val baseUrl = BuildConfig.LocalHost

    // Function to create a logging interceptor
    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }
    private val loggingClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .build()

    // Retrofit instances for both authenticated and unauthenticated services
    private val unauthenticatedRetrofit: Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(loggingClient)
            .build()

    override var userRepository: UserRepository = NetworkUserRepository(unauthenticatedRetrofit.create(UserApiService::class.java))


    // OkHttpClient with AuthInterceptor (for authenticated services)
    private var authenticatedClient: OkHttpClient  =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userRepository)) // Inject UserRepository
            .addInterceptor(loggingInterceptor())
            .build()

    fun reLoadAuthenticatedClient(){
        authenticatedClient =
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(userRepository)) // Inject UserRepository
                .addInterceptor(loggingInterceptor())
                .build()
    }




    // Make userApiService a regular var, so it can be reset
    private var _userApiService: UserApiService = unauthenticatedRetrofit.create(UserApiService::class.java)

    private var authenticatedRetrofit: Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(authenticatedClient)
            .build()

    private fun reloadAuthenticatedRetrofit(){
        reLoadAuthenticatedClient()
        authenticatedRetrofit =  Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(authenticatedClient)
            .build()
    }

    // Shift repository remains the same
    override var shiftRepository: ShiftRepository =
        NetworkShiftRepository(authenticatedRetrofit.create(ShiftApiService::class.java))


    // Method to switch to authenticated Retrofit
    override fun setUserApiServiceToAuthenticated() {
        _userApiService = authenticatedRetrofit.create(UserApiService::class.java)
        userRepository = NetworkUserRepository(_userApiService)

    }

    override fun clearUserApiServiceAuthorHeader() {
        _userApiService = unauthenticatedRetrofit.create(UserApiService::class.java)
        userRepository = NetworkUserRepository(_userApiService)
    }

    override fun updateNewAuthHeader()  {
        reloadAuthenticatedRetrofit()
         userRepository = NetworkUserRepository(authenticatedRetrofit.create(UserApiService::class.java))
        shiftRepository = NetworkShiftRepository(authenticatedRetrofit.create(ShiftApiService::class.java))
    }








}

