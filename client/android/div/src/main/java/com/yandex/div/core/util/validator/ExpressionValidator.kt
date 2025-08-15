package com.yandex.div.core.util.validator

internal class ExpressionValidator(
    allowEmpty: Boolean,
    val calculateExpression: () -> Boolean
) : BaseValidator(allowEmpty) {
    override fun validate(input: String): Boolean {
        return (allowEmpty && input.isEmpty()) || calculateExpression()
    }
}
