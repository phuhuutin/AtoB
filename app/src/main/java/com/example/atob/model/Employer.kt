package com.example.atob.model

import kotlinx.serialization.Serializable

@Serializable
data class Employer (
    val id: Long,
    val name: String,
    val address: Address
)
@Serializable
data class Address(
    val id: Long,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String,
    val latitude: Double? = null,
    val longitude: Double? = null
){
    override fun toString(): String {
        return "$street, $city, $state, $postalCode, $country"
    }
}