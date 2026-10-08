package com.yandex.div.core.type

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Formats dates the same way as `java.util.GregorianCalendar` does: dates before the Gregorian
 * cutover use the Julian calendar, and years before the common era are printed as era years.
 */
internal object DateTimeFormatter {

    private val GREGORIAN_CUTOVER_EPOCH_DAY = LocalDate(1582, 10, 15).toEpochDays()
    private const val JULIAN_DAY_NUMBER_OF_EPOCH = 2440588L

    @OptIn(ExperimentalTime::class)
    fun format(epochMillis: Long, timeZone: TimeZone): String {
        val dateTime = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
        return format(dateTime)
    }

    @Suppress("LocalVariableName")
    private fun format(dateTime: LocalDateTime): String {
        val date = toCalendarDate(dateTime.date)
        val yyyy = date.yearOfEra.toString()
        val MM = date.month.toString().padStart(2, '0')
        val DD = date.day.toString().padStart(2, '0')
        val hh = dateTime.hour.toString().padStart(2, '0')
        val mm = dateTime.minute.toString().padStart(2, '0')
        val ss = dateTime.second.toString().padStart(2, '0')
        return "$yyyy-$MM-$DD $hh:$mm:$ss"
    }

    private fun toCalendarDate(date: LocalDate): CalendarDate {
        val epochDay = date.toEpochDays()
        if (epochDay >= GREGORIAN_CUTOVER_EPOCH_DAY) {
            return CalendarDate(date.year.toLong(), date.month.number.toLong(), date.day.toLong())
        }
        return toJulianDate(epochDay)
    }

    /**
     * Richards' algorithm for converting a Julian day number to a Julian calendar date.
     * See https://en.wikipedia.org/wiki/Julian_day#Julian_or_Gregorian_calendar_from_Julian_day_number
     */
    private fun toJulianDate(epochDay: Long): CalendarDate {
        val c = epochDay + JULIAN_DAY_NUMBER_OF_EPOCH + 32082
        val d = (4 * c + 3).floorDiv(1461)
        val e = c - (1461 * d).floorDiv(4)
        val m = (5 * e + 2).floorDiv(153)
        val day = e - (153 * m + 2).floorDiv(5) + 1
        val month = m + 3 - 12 * m.floorDiv(10)
        val year = d - 4800 + m.floorDiv(10)
        return CalendarDate(year, month, day)
    }

    private class CalendarDate(
        val year: Long,
        val month: Long,
        val day: Long
    ) {

        val yearOfEra: Long
            get() = if (year > 0) year else 1 - year
    }
}
