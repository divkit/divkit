package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable

@Composable
internal fun DivVariableController.observedVariableValue(name: String): String? {
    val variable = get(name)
    if (variable == null) {
        reportError("Variable [$name] not found")
        return null
    }

    if (variable !is Variable.StringVariable) {
        reportError("Variable [$name] is not a string variable")
        return null
    }

    val state = remember(variable) {
        mutableStateOf(variable.getValue())
    }

    DisposableEffect(variable) {
        val observer: (Variable) -> Unit = { state.value = it.getValue() }
        variable.addObserver(observer)
        onDispose { variable.removeObserver(observer) }
    }

    return state.value as? String
}
