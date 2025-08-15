package com.yandex.div.core.util.validator

internal abstract class BaseValidator(
    val allowEmpty: Boolean
) {
    abstract fun validate(input: String): Boolean
}
