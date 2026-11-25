@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.evaluable.types

import kotlinx.datetime.TimeZone
import platform.Foundation.NSTimeZone
import com.yandex.div.core.type.DateTime as CoreDateTime

public actual class DateTime internal constructor(
    internal actual val timestampMillis: Long,
    internal val timeZone: NSTimeZone,
) : Comparable<DateTime> {

    actual override fun toString(): String =
        CoreDateTime.create(timestampMillis, TimeZone.of(timeZone.name)).toString()

    actual override fun equals(other: Any?): Boolean =
        other is DateTime && timestampMillis == other.timestampMillis

    actual override fun hashCode(): Int = timestampMillis.hashCode()

    actual override fun compareTo(other: DateTime): Int =
        timestampMillis.compareTo(other.timestampMillis)
}
