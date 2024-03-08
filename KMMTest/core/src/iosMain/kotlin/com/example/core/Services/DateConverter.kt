package com.example.core.Services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSinceReferenceDate

actual fun LocalDateTime.format(format: DateFormats): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = format.format
    return dateFormatter.stringFromDate(this.toNSDateComponents().date ?: NSDate())
}