package com.yandex.div.core.util.inputfilter

internal class InputFiltersHolder(private val filters: List<BaseInputFilter>) : BaseInputFilter {

    var currentValue = ""
    var cursorPosition = 0

    override fun checkValue(value: String) = filters.all { it.checkValue(value) }
}
