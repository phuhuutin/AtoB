package com.example.atob.model

import kotlinx.serialization.Serializable

@Serializable
data class InitialSetupDTO(
    val employerName: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
