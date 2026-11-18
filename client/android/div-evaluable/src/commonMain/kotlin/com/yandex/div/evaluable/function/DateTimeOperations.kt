@file:JvmName("DateTimeOperations")

package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime
import kotlin.jvm.JvmName

internal const val MILLIS_PER_SECOND = 1000L

internal expect fun DateTime(timestampMillis: Long, isLocal: Boolean): DateTime

internal expect fun DateTime.withTimestampMillis(timestampMillis: Long): DateTime

internal expect fun formatDateTime(dateTime: DateTime, pattern: String, local: Boolean, localeTag: String?): String
