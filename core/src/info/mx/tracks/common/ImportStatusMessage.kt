package info.mx.tracks.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class ImportStatusMessage(
    val message: String,
    val statusMessageType: StatusMessageType = StatusMessageType.INFO,
    val created: Long = System.currentTimeMillis() / 1000,
    val time: String = format(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.UTC))
)

enum class StatusMessageType {
    ERROR,
    INFO,
}

const val PATTERN = "HH:mm:ss"

fun format(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern(PATTERN)
    return date.format(formatter)
}