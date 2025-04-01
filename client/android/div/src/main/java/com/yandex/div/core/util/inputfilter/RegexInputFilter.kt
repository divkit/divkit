package com.yandex.div.core.util.inputfilter

internal class RegexInputFilter(pattern: String) : BaseInputFilter {

    private val regex = Regex(pattern)

    override fun checkValue(value: String) = regex.matches(value)
}
