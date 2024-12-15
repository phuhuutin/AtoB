package com.example.atob.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ClockInOutRecord(
    val id: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val clockInTime: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val clockOutTime: LocalDateTime? = null,
    val minuteWorked: Double,
    val shift_id: Int,
    val user_id: Int
)

@Serializable
data class ClockInOutRecordsUpdate(
    val id: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val clockInTime: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val clockOutTime: LocalDateTime? = null,
)