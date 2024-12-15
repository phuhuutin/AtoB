package com.example.atob.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Report(
    val id: Long,
    val type: ReportType,
    @Serializable(with = DateSerializer::class)
    val reportDate: LocalDate,
    val details: String,
    var userId: Long,
    var completed: Boolean,
    var username: String,
    val attendanceRecord: AttendanceRecord? = null,
    val clock: ClockInOutRecordsUpdate,
    val shift: SimpleShift
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Report
        return this.id == other.id
    }
}
@Serializable
data class ReportDTO(
    val type: ReportType,
    val details: String,
    val attendanceRecordId: Long? = null,
    var clockInOutRecordId: Long? = null,
    var shiftId: Long? = null
)

@Serializable
enum class ReportType {
    SHIFT, ATTENDANCE, CLOCK
}