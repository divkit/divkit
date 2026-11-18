@file:JvmName("DateTimeOperationsJvm")

package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal actual fun DateTime(timestampMillis: Long, isLocal: Boolean): DateTime {
    return DateTime(timestampMillis, if (isLocal) TimeZone.getDefault() else TimeZone.getTimeZone("UTC"))
}

internal actual fun formatDateTime(dateTime: DateTime, pattern: String, local: Boolean, localeTag: String?): String {
    val locale = if (localeTag == null) Locale.getDefault() else Locale.Builder().setLanguageTag(localeTag).build()
    val formatter = SimpleDateFormat(pattern, locale)
    formatter.timeZone = if (local) TimeZone.getDefault() else TimeZone.getTimeZone("UTC")
    return formatter.format(dateTime.toDate())
}

internal actual fun DateTime.withTimestampMillis(timestampMillis: Long): DateTime {
    return DateTime(timestampMillis, timezone)
}

internal fun DateTime.toDate(): Date = Date(timestampMillis)
