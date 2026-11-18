package com.yandex.div.evaluable.types

import java.util.TimeZone

public actual class DateTime(
    internal actual val timestampMillis: Long,
    internal val timezone: TimeZone,
) : Comparable<DateTime> {

    actual override fun toString(): String {
        return DateTimeFormatter.format(timezone, timestampMillis)
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is DateTime) {
            return false
        }

        return timestampMillis == other.timestampMillis
    }

    actual override fun hashCode(): Int {
        return timestampMillis.hashCode()
    }

    actual override fun compareTo(other: DateTime): Int {
        return timestampMillis.compareTo(other.timestampMillis)
    }
}
