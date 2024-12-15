package com.example.atob.model

import kotlinx.serialization.Serializable

@Serializable
data class Payroll(
    val id: Long,
    val totalHoursWorked: Double,
    val totalPay: Double,
    val date: String, // ISO 8601 format: YYYY-MM-DD
    val payRate: Double,
    val shift_id: Long = 0L
)