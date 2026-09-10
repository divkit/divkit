package com.yandex.div.compose.variables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.data.Variable
import com.yandex.div.json.missingVariable
import com.yandex.div2.DivEvaluableType

@Composable
internal fun mutableStateFromVariable(
    variableName: String,
    defaultValue: String
): MutableState<String> {
    return mutableStateFromStringVariable(variableName)
        ?: remember { mutableStateOf(defaultValue) }
}

@Composable
internal fun mutableStateFromVariable(
    variableName: String,
    defaultValue: Boolean
): MutableState<Boolean> {
    return mutableStateFromBooleanVariable(variableName)
        ?: remember { mutableStateOf(defaultValue) }
}

@Composable
internal fun mutableStateFromVariable(
    variableName: String,
    defaultValue: Long
): MutableState<Long> {
    return mutableStateFromIntegerVariable(variableName)
        ?: remember { mutableLongStateOf(defaultValue) }
}

@Composable
internal fun mutableStateFromStringVariable(variableName: String): MutableState<String>? {
    return rememberVariableMutableState(
        variableName = variableName,
        readValue = { it.getValue().toString() },
        validate = { variable ->
            if (variable is Variable.StringVariable ||
                variable.isProperty(DivEvaluableType.STRING)
            ) {
                null
            } else {
                "variable [$variableName] is not a string variable"
            }
        },
    )
}

@Composable
internal fun mutableStateFromBooleanVariable(variableName: String): MutableState<Boolean>? {
    return rememberVariableMutableState(
        variableName = variableName,
        readValue = { it.getValue() as Boolean },
        validate = { variable ->
            if (variable is Variable.BooleanVariable ||
                variable.isProperty(DivEvaluableType.BOOLEAN)
            ) {
                null
            } else {
                "variable [$variableName] is not a boolean variable"
            }
        },
    )
}

@Composable
internal fun mutableStateFromIntegerVariable(variableName: String): MutableState<Long>? {
    return rememberVariableMutableState(
        variableName = variableName,
        readValue = { it.getValue() as Long },
        validate = { variable ->
            if (variable is Variable.IntegerVariable ||
                variable.isProperty(DivEvaluableType.INTEGER)
            ) {
                null
            } else {
                "variable [$variableName] is not an integer variable"
            }
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
            reporter.reportError(missingVariable(variableName))
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
            writeValue = { variable, value ->
                try {
                    variable.setValueDirectly(value)
                } catch (e: Exception) {
                    reporter.reportError(e)
                }
            },
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
            writeValue(variable, newValue)
            _value = readValue(variable)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }

    fun onVariableChanged(variable: Variable) {
        _value = readValue(variable)
    }
}

private fun Variable.isProperty(type: DivEvaluableType): Boolean {
    return this is Variable.PropertyVariable && valueType == type
}
