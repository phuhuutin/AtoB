package com.example.atob.model
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val payRate: Double,
    val role: String,
  //  val pickedShifts: List<Shift>? = null, // Nullable list for shifts
   // val attendancePoints: AttendancePoints? = null // Nullable attendance points
)

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String
)
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class AttendancePoints(
    val id: Long,
    val points: Int // Add other fields as necessary
)