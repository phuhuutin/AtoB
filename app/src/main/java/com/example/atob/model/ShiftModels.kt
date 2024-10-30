package com.example.atob.model

import android.annotation.SuppressLint
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class Shift(
    val id: Long = 0L,
    @Serializable(with = DateSerializer::class)
    val date: LocalDate,  // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endTime: LocalDateTime,
    val workerLimit: Int,  // Maximum number of employees who can pick the shift
    var currentWorkers: Int = 0,  // Track how many employees have picked the shift
    val employees: MutableList<User> = mutableListOf(),  // Employees who picked this shift
    val postedBy: User,  // Manager who posted the shift
    @Serializable(with = UUIDSerializer::class)
    val jobId: UUID,  // Unique job ID
    val shiftFull: Boolean
)
@Serializable
data class ShiftDTO(
    @Serializable(with = DateSerializer::class)
    val date: LocalDate,           // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)

    val startTime: LocalDateTime,   // Start time of the shift
    @Serializable(with = LocalDateTimeSerializer::class)

    val endTime: LocalDateTime,     // End time of the shift
    val workerLimit: Int,           // Maximum number of employees who can pick the shift
    val postedById: Long            // ID of the manager who is posting the shift
)



object DateSerializer : KSerializer<LocalDate> {
    @SuppressLint("NewApi")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @SuppressLint("NewApi")
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)


    @SuppressLint("NewApi")
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    @SuppressLint("NewApi")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    @SuppressLint("NewApi")
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    @SuppressLint("NewApi")
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}