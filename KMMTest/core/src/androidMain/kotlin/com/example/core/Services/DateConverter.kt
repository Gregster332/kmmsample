package com.example.core.Services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

actual fun LocalDateTime.format(
    format: DateFormats
): String = DateTimeFormatter.ofPattern(format.format).format(this.toJavaLocalDateTime())