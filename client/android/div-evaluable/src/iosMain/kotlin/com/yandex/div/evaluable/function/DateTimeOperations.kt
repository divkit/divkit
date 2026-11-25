@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.timeZoneForSecondsFromGMT

private fun timeZone(isLocal: Boolean): NSTimeZone =
    if (isLocal) NSTimeZone.localTimeZone else NSTimeZone.timeZoneForSecondsFromGMT(0)

internal actual fun DateTime(timestampMillis: Long, isLocal: Boolean): DateTime =
    DateTime(timestampMillis, timeZone(isLocal))

internal actual fun DateTime.withTimestampMillis(timestampMillis: Long): DateTime =
    DateTime(timestampMillis, timeZone)

internal actual fun formatDateTime(dateTime: DateTime, pattern: String, local: Boolean, localeTag: String?): String {
    val formatter = NSDateFormatter().apply {
        timeZone = timeZone(local)
        if (localeTag != null) locale = NSLocale(localeIdentifier = localeTag)
        dateFormat = pattern.withZeroPaddedDayFields(locale)
    }
    return formatter.stringFromDate(dateTime.timestampMillis.toNativeDate())
}

internal fun Long.toNativeDate(): NSDate =
    NSDate.dateWithTimeIntervalSince1970(toDouble() / MILLIS_PER_SECOND)

// Foundation requires explicit padding for three or more 'd' characters, as in Swift DivKit.
private fun String.withZeroPaddedDayFields(locale: NSLocale): String {
    if (!contains("ddd")) return this
    val zero = NSNumberFormatter().apply { this.locale = locale }.stringFromNumber(NSNumber(int = 0)) ?: "0"
    return buildString {
        var quoted = false
        var days = 0

        fun appendDays() {
            repeat((days - 2).coerceAtLeast(0)) { append(zero) }
            repeat(days.coerceAtMost(2)) { append('d') }
            days = 0
        }

        for (char in this@withZeroPaddedDayFields) {
            if (char == 'd' && !quoted) {
                days++
            } else {
                appendDays()
                if (char == '\'') quoted = !quoted
                append(char)
            }
        }
        appendDays()
    }
}
