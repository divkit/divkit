package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromBooleanVariable
import com.yandex.div2.DivInputValidator

@Composable
internal fun List<DivInputValidator>.validate(text: String) {
    forEach { validator ->
        val result = mutableStateFromBooleanVariable(validator.variableName) ?: return@forEach
        result.value = when (validator) {
            is DivInputValidator.Regex -> validator.isValid(text)
            is DivInputValidator.Expression -> validator.isValid(text)
        }
    }
}

@Composable
private fun DivInputValidator.Regex.isValid(text: String): Boolean {
    val allowEmpty = value.allowEmpty.observedValue()
    if (allowEmpty && text.isEmpty()) {
        return true
    }

    val pattern = value.pattern.observedValue()
    val regex = rememberRegex(pattern) ?: return true
    return regex.matches(text)
}

@Composable
private fun DivInputValidator.Expression.isValid(text: String): Boolean {
    val allowEmpty = value.allowEmpty.observedValue()
    if (allowEmpty && text.isEmpty()) {
        return true
    }

    val condition = value.condition.observedValue()
    return condition
}

private val DivInputValidator.variableName: String
    get() = when (this) {
        is DivInputValidator.Regex -> value.variable
        is DivInputValidator.Expression -> value.variable
    }
