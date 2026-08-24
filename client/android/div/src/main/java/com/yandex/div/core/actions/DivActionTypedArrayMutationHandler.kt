package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.util.asList
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import com.yandex.div2.DivActionTyped
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedArrayMutationHandler @Inject constructor(
    private val variableMutationHandler: VariableMutationHandler,
) : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {
        is DivActionTyped.ArrayInsertValue -> {
            handle(action.value, resolver, view.errorCollector)
            true
        }
        is DivActionTyped.ArrayRemoveValue -> {
            handle(action.value, resolver, view.errorCollector)
            true
        }
        is DivActionTyped.ArraySetValue -> {
            handle(action.value, resolver, view.errorCollector)
            true
        }
        else -> false
    }

    private fun handle(
        action: DivActionArrayInsertValue,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index?.evaluate(resolver)?.toInt()
        val newValue = action.value.evaluateAsPrimitive(resolver)
        updateVariable(variableName, resolver, errorCollector) { array ->
            val length = array.length()
            when (index) {
                null, length -> array.mutate { add(newValue) }
                in 0 until length -> array.mutate { add(index, newValue) }
                else -> {
                    errorCollector.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }

    private fun handle(
        action: DivActionArrayRemoveValue,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index.evaluate(resolver).toInt()
        updateVariable(variableName, resolver, errorCollector) { array ->
            val length = array.length()
            when (index) {
                in 0 until length -> array.mutate { removeAt(index) }
                else -> {
                    errorCollector.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }

    private fun handle(
        action: DivActionArraySetValue,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index.evaluate(resolver).toInt()
        val newValue = action.value.evaluateAsPrimitive(resolver)
        updateVariable(variableName, resolver, errorCollector) { array ->
            val length = array.length()
            when (index) {
                in 0 until length -> array.mutate { this[index] = newValue }
                else -> {
                    errorCollector.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }

    private fun updateVariable(
        name: String,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
        valueMutation: (JSONArray) -> JSONArray
    ) {
        variableMutationHandler.setVariable(name, resolver, errorCollector) { variable: Variable ->
            variable.also { it.mutate(valueMutation, errorCollector) }
        }
    }

    private fun Variable.mutate(valueMutation: (JSONArray) -> JSONArray, errorCollector: ErrorCollector) {
        val arrayVariable = this as? Variable.ArrayVariable
            ?: return errorCollector.logError(IllegalArgumentException("Action requires array variable"))

        val value = arrayVariable.getValue() as? JSONArray
            ?: return errorCollector.logError(IllegalArgumentException("Invalid variable value"))

        arrayVariable.set(valueMutation(value))
    }
}

private fun JSONArray.mutate(action: MutableList<Any>.() -> Unit): JSONArray {
    return asList<Any>()
        .toMutableList()
        .apply(action::invoke)
        .let(::JSONArray)
}
