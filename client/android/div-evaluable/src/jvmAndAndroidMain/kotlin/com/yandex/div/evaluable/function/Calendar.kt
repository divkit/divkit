package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime
import java.util.GregorianCalendar
import java.util.Calendar as JavaCalendar

internal actual class Calendar actual constructor(
    dateTime: DateTime
) {

    private val calendar = dateTime.toCalendar()

    actual var timeInMillis: Long
        get() = calendar.timeInMillis
        set(value) {
            calendar.timeInMillis = value
        }

    actual fun get(field: Field): Int {
        return calendar.get(field.toJavaCalendarField())
    }

    actual fun set(field: Field, value: Int) {
        calendar.set(field.toJavaCalendarField(), value)
    }

    actual fun getActualMaximum(field: Field): Int {
        return calendar.getActualMaximum(field.toJavaCalendarField())
    }

    actual enum class Field {
        YEAR,
        MONTH,
        DAY_OF_MONTH,
        DAY_OF_WEEK,
        HOUR_OF_DAY,
        MINUTE,
        SECOND,
        MILLISECOND
    }
}

private fun DateTime.toCalendar(): JavaCalendar {
    val calendar = GregorianCalendar.getInstance()
    calendar.timeZone = timezone
    calendar.timeInMillis = timestampMillis
    return calendar
}

private fun Calendar.Field.toJavaCalendarField(): Int = when (this) {
   Calendar.Field.YEAR -> JavaCalendar.YEAR
   Calendar.Field.MONTH -> JavaCalendar.MONTH
   Calendar.Field.DAY_OF_MONTH -> JavaCalendar.DAY_OF_MONTH
   Calendar.Field.DAY_OF_WEEK -> JavaCalendar.DAY_OF_WEEK
   Calendar.Field.HOUR_OF_DAY -> JavaCalendar.HOUR_OF_DAY
   Calendar.Field.MINUTE -> JavaCalendar.MINUTE
   Calendar.Field.SECOND -> JavaCalendar.SECOND
   Calendar.Field.MILLISECOND -> JavaCalendar.MILLISECOND
}
