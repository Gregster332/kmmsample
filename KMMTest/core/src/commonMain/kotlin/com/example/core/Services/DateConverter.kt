package com.example.core.Services

import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

enum class DateFormats(val format: String) {
    HH_MM("HH:mm")
}

object DateConverter {
    private val timeZoe = TimeZone.currentSystemDefault()

    val current = Clock.System.now().toString()

    fun convert(string: String, format: DateFormats): String {
        val instant = Instant.parse(string)
        return instant.toLocalDateTime(timeZoe).format(format)
    }
}

expect fun LocalDateTime.format(format: DateFormats): String