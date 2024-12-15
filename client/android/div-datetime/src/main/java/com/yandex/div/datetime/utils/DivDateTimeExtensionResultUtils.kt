package com.yandex.div.datetime.utils

import java.util.Calendar
import java.util.Date

internal fun Calendar.setSelectedDate(date: Date) {
    time = date
}

internal fun Calendar.setSelectedDate(year: Int, month: Int, day: Int) {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, month)
    set(Calendar.DAY_OF_MONTH, day)
}

internal fun Calendar.setSelectedTime(hours: Int, minutes: Int) {
    set(Calendar.HOUR_OF_DAY, hours)
    set(Calendar.MINUTE, minutes)
}