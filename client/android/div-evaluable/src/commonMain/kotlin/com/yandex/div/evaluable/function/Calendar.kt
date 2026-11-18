package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.types.DateTime

internal expect class Calendar(dateTime: DateTime) {

    var timeInMillis: Long

    fun get(field: Field): Int

    fun set(field: Field, value: Int)

    fun getActualMaximum(field: Field): Int

    enum class Field {
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
