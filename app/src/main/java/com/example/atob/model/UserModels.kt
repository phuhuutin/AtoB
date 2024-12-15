package com.example.atob.model
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val payRate: Double,
    val role: String,
    val employer: Employer,
    val attendancePoints: AttendancePoints? = null
//  val pickedShifts: List<Shift>? = null, // Nullable list for shifts
   // val attendancePoints: AttendancePoints? = null // Nullable attendance points
)

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    val employerId: Long,
)
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)


@Serializable
data class ChangePasswordRequest(
    var username: String,
    var oldPassword: String,
    var newPassword: String
)

@Serializable
data class PayRateUpdateRequest(
    val payRate: Double,
    val userId: Long
)