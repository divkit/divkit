package com.yandex.div.core.util.validator

internal class RegexValidator(
    private val regex: Regex,
    allowEmpty: Boolean
) : BaseValidator(allowEmpty) {
    override fun validate(input: String): Boolean {
        return (allowEmpty && input.isEmpty()) || regex.matches(input)
    }
}
