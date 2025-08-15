package com.yandex.div.evaluable.types

import java.util.Calendar
import java.util.SimpleTimeZone
import java.util.TimeZone

private const val TO_MILLIS = 60 * 1000

class DateTime(
    internal val timestampMillis: Long,
    internal val timezone: TimeZone,
) : Comparable<DateTime> {
    private val calendar by lazy(LazyThreadSafetyMode.NONE) {
        Calendar.getInstance(utcTimezone).apply { timeInMillis = timestampMillis }
    }
    internal val timezoneMinutes = timezone.rawOffset / 60
    private val timestampUtc = timestampMillis - (timezoneMinutes * TO_MILLIS)

    override fun toString(): String {
        return formatDate(calendar)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is DateTime) {
            return false
        }

        return timestampUtc == other.timestampUtc
    }

    override fun hashCode(): Int {
        return timestampUtc.hashCode()
    }

    companion object {
        private val utcTimezone = SimpleTimeZone(0, "UTC")

        internal fun formatDate(c: Calendar): String {
            val yyyy = c.get(Calendar.YEAR).toString()
            val MM = (c.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val DD = c.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
            val hh = c.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
            val mm = c.get(Calendar.MINUTE).toString().padStart(2, '0')
            val ss = c.get(Calendar.SECOND).toString().padStart(2, '0')
            return "$yyyy-$MM-$DD $hh:$mm:$ss"
        }
    }

    override fun compareTo(other: DateTime): Int {
        return timestampUtc.compareTo(other.timestampUtc)
    }
}
