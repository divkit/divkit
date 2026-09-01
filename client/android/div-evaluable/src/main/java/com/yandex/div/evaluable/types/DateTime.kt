package com.yandex.div.evaluable.types

import java.util.TimeZone

class DateTime(
    internal val timestampMillis: Long,
    internal val timezone: TimeZone,
) : Comparable<DateTime> {

    override fun toString(): String {
        return DateTimeFormatter.format(timezone, timestampMillis)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is DateTime) {
            return false
        }

        return timestampMillis == other.timestampMillis
    }

    override fun hashCode(): Int {
        return timestampMillis.hashCode()
    }

    override fun compareTo(other: DateTime): Int {
        return timestampMillis.compareTo(other.timestampMillis)
    }
}
