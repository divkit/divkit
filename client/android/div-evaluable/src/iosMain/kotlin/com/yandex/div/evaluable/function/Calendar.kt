@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime
import kotlinx.cinterop.useContents
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnit
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitEra
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitNanosecond
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitWeekday
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import kotlin.math.roundToLong

internal actual class Calendar actual constructor(dateTime: DateTime) {

    private val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian).apply {
        timeZone = dateTime.timeZone
    }

    actual var timeInMillis: Long = dateTime.timestampMillis

    actual fun get(field: Field): Int {
        if (field == Field.MILLISECOND) return milliseconds.toInt()
        val value = calendar.component(field.unit, fromDate = wholeSecondDate).toInt()
        return if (field == Field.MONTH) value - 1 else value
    }

    actual fun set(field: Field, value: Int) {
        val date = wholeSecondDate
        val fraction = milliseconds
        when (field) {
            Field.MILLISECOND -> adjustTime(date, value.toLong())
            Field.DAY_OF_WEEK -> {
                val dayOffset = value.toLong() - calendar.component(NSCalendarUnitWeekday, fromDate = date)
                val adjusted = calendar.dateByAddingUnit(
                    NSCalendarUnitDay,
                    value = dayOffset,
                    toDate = date,
                    options = 0u,
                )
                adjustTime(adjusted, fraction)
            }
            else -> {
                val components = calendar.components(DATE_FIELDS, fromDate = date)
                when (field) {
                    Field.YEAR -> components.year = value.toLong()
                    Field.MONTH -> components.month = value.toLong() + 1
                    Field.DAY_OF_MONTH -> components.day = value.toLong()
                    Field.HOUR_OF_DAY -> components.hour = value.toLong()
                    Field.MINUTE -> components.minute = value.toLong()
                    Field.SECOND -> components.second = value.toLong()
                    Field.DAY_OF_WEEK, Field.MILLISECOND -> error("Handled separately")
                }
                adjustTime(calendar.dateFromComponents(components), fraction)
            }
        }
    }

    actual fun daysInMonth(): Int = calendar.rangeOfUnit(
        NSCalendarUnitDay, inUnit = NSCalendarUnitMonth, forDate = wholeSecondDate,
    ).useContents { length.toInt() }

    private val wholeSecondDate: NSDate
        get() = wholeSecondsToNativeDate(timeInMillis)

    private val milliseconds: Long
        get() = timeInMillis.mod(MILLIS_PER_SECOND)

    private fun adjustTime(date: NSDate?, fraction: Long) {
        requireNotNull(date) { "Date components could not be resolved" }
        timeInMillis = date.timeIntervalSince1970.roundToLong() * MILLIS_PER_SECOND + fraction
    }

    actual enum class Field(internal val unit: NSCalendarUnit) {
        YEAR(NSCalendarUnitYear),
        MONTH(NSCalendarUnitMonth),
        DAY_OF_MONTH(NSCalendarUnitDay),
        DAY_OF_WEEK(NSCalendarUnitWeekday),
        HOUR_OF_DAY(NSCalendarUnitHour),
        MINUTE(NSCalendarUnitMinute),
        SECOND(NSCalendarUnitSecond),
        MILLISECOND(NSCalendarUnitNanosecond),
    }
}

private fun wholeSecondsToNativeDate(timestampMillis: Long): NSDate =
    NSDate.dateWithTimeIntervalSince1970(timestampMillis.floorDiv(MILLIS_PER_SECOND).toDouble())

private val DATE_FIELDS = NSCalendarUnitEra or NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
    NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond
