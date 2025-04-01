package com.yandex.div.core.util.inputfilter

internal interface BaseInputFilter {
    fun checkValue(value: String): Boolean
}
