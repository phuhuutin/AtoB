package com.example.atob.model
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AttendanceRecord(
    val id: Long,
    @Serializable(with = DateSerializer::class)
    val date: LocalDate, // Date of the record

    val reason: String, // Attendance reason with associated points

    val shiftId: Long, // Expose only the shift ID
    )
@Serializable
data  class AttendancePoints(
    val id: Long? = null,
     val userId: Long? = null, // Expose only the user's ID
     val attendanceRecords: List<AttendanceRecord> = emptyList() ,// List of associated records
        val points: Double // Total points earned
)