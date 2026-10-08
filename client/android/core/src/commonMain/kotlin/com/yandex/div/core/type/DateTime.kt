package com.yandex.div.core.type

import kotlinx.datetime.TimeZone

public class DateTime private constructor(
    public val epochMillis: Long,
    public val timeZone: TimeZone,
) : Comparable<DateTime> {

    override fun toString(): String {
        return DateTimeFormatter.format(epochMillis, timeZone)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is DateTime) {
            return false
        }

        return epochMillis == other.epochMillis
    }

    override fun hashCode(): Int {
        return epochMillis.hashCode()
    }

    override fun compareTo(other: DateTime): Int {
        return epochMillis.compareTo(other.epochMillis)
    }

    public companion object {

        @JvmStatic
        @JvmOverloads
        public fun create(epochMillis: Long, timeZone: TimeZone = TimeZone.UTC): DateTime {
            return DateTime(epochMillis, timeZone)
        }
    }
}
