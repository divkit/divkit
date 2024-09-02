package com.yandex.div.core.actions

import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.asList
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import com.yandex.div2.DivActionTyped
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedArrayMutationHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {
        is DivActionTyped.ArrayInsertValue -> {
            handle(action.value, view, resolver)
            true
        }
        is DivActionTyped.ArrayRemoveValue -> {
            handle(action.value, view, resolver)
            true
        }
        is DivActionTyped.ArraySetValue -> {
            handle(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handle(
        action: DivActionArrayInsertValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index?.evaluate(resolver)?.toInt()
        val newValue = action.value.evaluate(resolver)
        view.updateVariable(variableName, resolver) { array ->
            val length = array.length()
            when (index) {
                null, length -> array.mutate { add(newValue) }
                in 0 until length -> array.mutate { add(index, newValue) }
                else -> {
                    view.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }

    private fun handle(
        action: DivActionArrayRemoveValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index.evaluate(resolver).toInt()
        view.updateVariable(variableName, resolver) { array ->
            val length = array.length()
            when (index) {
                in 0 until length -> array.mutate { removeAt(index) }
                else -> {
                    view.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }

    private fun handle(
        action: DivActionArraySetValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index.evaluate(resolver).toInt()
        val newValue = action.value.evaluate(resolver)
        view.updateVariable(variableName, resolver) { array ->
            val length = array.length()
            when (index) {
                in 0 until length -> array.mutate { this[index] = newValue }
                else -> {
                    view.logError(IndexOutOfBoundsException(
                        "Index out of bound ($index) for mutation $variableName ($length)"
                    ))
                    array
                }
            }
        }
    }
}

private fun JSONArray.mutate(action: MutableList<Any>.() -> Unit): JSONArray {
    return asList<Any>()
        .toMutableList()
        .apply(action::invoke)
        .let(::JSONArray)
}

private fun Div2View.updateVariable(
    name: String,
    resolver: ExpressionResolver,
    valueMutation: (JSONArray) -> JSONArray
) {
    VariableMutationHandler.setVariable(this, name, resolver) { variable: Variable ->
        if (variable !is Variable.ArrayVariable) {
            view.logError(
                IllegalArgumentException("Action requires array variable")
            )
            return@setVariable variable
        }

        val value = variable.getValue() as? JSONArray
        if (value == null) {
            view.logError(IllegalArgumentException("Invalid variable value"))
            return@setVariable variable
        }

        variable.set(valueMutation(value))
        return@setVariable variable
    }
}
