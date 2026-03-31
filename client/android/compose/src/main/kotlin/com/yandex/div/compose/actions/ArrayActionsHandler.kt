package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.map
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import org.json.JSONArray
import javax.inject.Inject

internal class ArrayActionsHandler @Inject constructor(
    private val reporter: DivReporter
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionArrayInsertValue
    ) {
        val expressionResolver = context.expressionResolver
        val variableWithValue = getArrayVariable(action.variableName, expressionResolver) ?: return
        val variable = variableWithValue.variable
        val array = variableWithValue.value
        val valueToInsert = action.value.evaluateAsPrimitive(expressionResolver)
        val length = array.length()
        when (val index = action.index?.evaluate(expressionResolver)?.toInt()) {
            null, length ->
                variable.set(array) { add(valueToInsert) }

            in 0 until length ->
                variable.set(array) { add(index, valueToInsert) }

            else -> reporter.reportError(
                "Unable to insert value into ${variable.name}: index ($index) out of bounds"
            )
        }
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionArrayRemoveValue
    ) {
        val expressionResolver = context.expressionResolver
        val variableWithValue = getArrayVariable(action.variableName, expressionResolver) ?: return
        val variable = variableWithValue.variable
        val array = variableWithValue.value
        when (val index = action.index.evaluate(expressionResolver).toInt()) {
            in 0 until array.length() -> variable.set(array) { removeAt(index) }
            else -> reporter.reportError(
                "Unable to remove value from ${variable.name}: index ($index) out of bounds"
            )
        }
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionArraySetValue
    ) {
        val expressionResolver = context.expressionResolver
        val variableWithValue = getArrayVariable(action.variableName, expressionResolver) ?: return
        val variable = variableWithValue.variable
        val array = variableWithValue.value
        when (val index = action.index.evaluate(expressionResolver).toInt()) {
            in 0 until array.length() ->
                variable.set(array) {
                    this[index] = action.value.evaluateAsPrimitive(expressionResolver)
                }

            else -> reporter.reportError(
                "Unable to set value in ${variable.name}: index ($index) out of bounds"
            )
        }
    }

    private fun getArrayVariable(
        variableName: Expression<String>,
        expressionResolver: ExpressionResolver
    ): VariableWithValue? {
        val variableName = variableName.evaluate(expressionResolver)
        val variable = expressionResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return null
        }

        val arrayVariable = variable as? Variable.ArrayVariable
        val array = arrayVariable?.getValue() as? JSONArray
        if (array == null) {
            reporter.reportError("Variable is not an array variable: $variableName")
            return null
        }

        return VariableWithValue(arrayVariable, array)
    }
}

private class VariableWithValue(
    val variable: Variable.ArrayVariable,
    val value: JSONArray
)

private fun Variable.ArrayVariable.set(array: JSONArray, action: MutableList<Any>.() -> Unit) {
    set(
        JSONArray(
            array.map { it }.toMutableList().apply(action::invoke)
        )
    )
}
