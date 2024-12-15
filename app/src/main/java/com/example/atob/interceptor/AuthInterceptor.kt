package com.example.atob.interceptor

import android.util.Log
import com.example.atob.data.UserRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userRepository: UserRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val encodedCredentials = userRepository.getEncodedCredentials()
        val requestBuilder = chain.request().newBuilder()
        Log.e("ChangePassword", "chain : ${chain.request().url}")
        Log.e("AuthInterceptor", "Encoded Credentials: $encodedCredentials")
        Log.e("ChangePassword", "new credentials loading: $encodedCredentials")

        // Add Authorization header if credentials are available
        if (encodedCredentials.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Basic $encodedCredentials")
        }

        return chain.proceed(requestBuilder.build())
    }
}
