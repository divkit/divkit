package com.yandex.div.evaluable.types

import java.util.Calendar
import java.util.TimeZone

internal object DateTimeFormatter {

    private val calendarCache = mutableMapOf<TimeZone, Calendar>()

    fun format(timeZone: TimeZone, timeInMillis: Long): String {
        val calendar = getCalendar(timeZone, timeInMillis)
        return format(calendar)
    }

    private fun getCalendar(timeZone: TimeZone, timeInMillis: Long): Calendar {
        val calendar = synchronized(calendarCache) {
            calendarCache.getOrPut(timeZone) { Calendar.getInstance(timeZone) }
        }
        calendar.timeInMillis = timeInMillis
        return calendar
    }

    private fun format(calendar: Calendar): String {
        val yyyy = calendar.get(Calendar.YEAR).toString()
        val MM = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val DD = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val hh = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val mm = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
        val ss = calendar.get(Calendar.SECOND).toString().padStart(2, '0')
        return "$yyyy-$MM-$DD $hh:$mm:$ss"
    }
}
