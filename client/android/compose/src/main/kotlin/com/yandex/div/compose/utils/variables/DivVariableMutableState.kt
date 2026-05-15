package com.yandex.div.compose.utils.variables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.data.Variable

@Composable
internal fun mutableStateFromVariable(variableName: String, defaultValue: String): MutableState<String> {
    return mutableStateFromStringVariable(variableName) ?: remember { mutableStateOf(defaultValue) }
}

@Composable
internal fun mutableStateFromVariable(variableName: String, defaultValue: Boolean): MutableState<Boolean> {
    return mutableStateFromBooleanVariable(variableName) ?: remember { mutableStateOf(defaultValue) }
}

@Composable
internal fun mutableStateFromStringVariable(variableName: String): MutableState<String>? {
    return rememberVariableMutableState(
        variableName = variableName,
        readValue = { it.getValue().toString() },
        validate = { variable ->
            if (variable is Variable.StringVariable) null
            else "variable [$variableName] is not a string variable"
        },
    )
}

@Composable
internal fun mutableStateFromBooleanVariable(variableName: String): MutableState<Boolean>? {
    return rememberVariableMutableState(
        variableName = variableName,
        readValue = { it.getValue() as Boolean },
        validate = { variable ->
            if (variable is Variable.BooleanVariable) null
            else "variable [$variableName] is not a boolean variable"
        },
    )
}

@Composable
private fun <T : Any> rememberVariableMutableState(
    variableName: String,
    readValue: (Variable) -> T,
    validate: (Variable) -> String?,
): MutableState<T>? {
    val controller = LocalComponent.current.variableController
    val reporter = LocalComponent.current.reporter
    val variable = remember(variableName, controller) {
        val variable = controller.get(variableName)

        if (variable == null) {
            reporter.reportError("variable [$variableName] not found")
            null
        } else {
            val error = validate(variable)
            if (error != null) {
                reporter.reportError(error)
                null
            } else {
                variable
            }
        }
    }

    if (variable == null) {
        return null
    }

    val state = remember(variable) {
        DivVariableMutableState(
            variable = variable,
            readValue = readValue,
            writeValue = { variable, value -> variable.setValueDirectly(value) },
        )
    }

    DisposableEffect(variable) {
        val observer: (Variable) -> Unit = { state.onVariableChanged(it) }
        variable.addObserver(observer)
        onDispose { variable.removeObserver(observer) }
    }

    return state
}

private class DivVariableMutableState<T : Any>(
    private val variable: Variable,
    private val readValue: (Variable) -> T,
    private val writeValue: (Variable, T) -> Unit,
) : MutableState<T> {
    private var _value by mutableStateOf(readValue(variable))

    override var value: T
        get() = _value
        set(newValue) {
            _value = newValue
            writeValue(variable, newValue)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }

    fun onVariableChanged(variable: Variable) {
        _value = readValue(variable)
    }
}
