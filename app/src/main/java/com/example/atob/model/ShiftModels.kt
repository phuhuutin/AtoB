package com.example.atob.model

import android.annotation.SuppressLint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
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
data class UserShift(
    val id: Long = 0L,
    @Serializable(with = DateSerializer::class)
    val date: LocalDate,  // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    var startTime: LocalDateTime,
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    var endTime: LocalDateTime,
    val clock: ClockInOutRecord? = null,
    val employer: Employer
)

@Serializable
data class FindShift(
    val id: Long = 0L,
    @Serializable(with = DateSerializer::class)
    val date: LocalDate,  // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endTime: LocalDateTime,
    val shiftFull: Boolean,
    val employees: Set<User> = emptySet(),
    val workerLimit: Int,
    val currentWorkers: Int,
    val postedBy: User,
    val employer: Employer

){
    fun toUserShift(username: String): UserShift {
        return UserShift(
            id = id,
            date = date,
            startTime = startTime,
            username = username,
            endTime = endTime,
            clock = null,
            employer = employer
        )
    }
}

@Serializable
data class SimpleShift(
    val id: Long = 0L,
    @Serializable(with = LocalDateTimeSerializer::class)
    var startTime: LocalDateTime,  // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    var endTime: LocalDateTime
)
@Serializable
data class ShiftDTO(
    @Serializable(with = DateSerializer::class)
    val date: LocalDate,           // The date of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime? = null,   // Start time of the shift
    @Serializable(with = LocalDateTimeSerializer::class)
    val endTime: LocalDateTime? = null,     // End time of the shift
    val workerLimit: Int,           // Maximum number of employees who can pick the shift
    var postedById: Long = 1L,
    var employerId: Long = 1L,
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