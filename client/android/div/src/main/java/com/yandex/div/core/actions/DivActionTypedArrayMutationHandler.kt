package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.asList
import com.yandex.div.json.expressions.ExpressionResolver
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
            handleInsert(action, view, resolver)
            true
        }

        is DivActionTyped.ArrayRemoveValue -> {
            handleRemove(action, view, resolver)
            true
        }

        else -> false
    }

    private fun handleInsert(action: DivActionTyped.ArrayInsertValue, view: Div2View, resolver: ExpressionResolver) {
        val variableName = action.value.variableName.evaluate(resolver)
        val index = action.value.index?.evaluate(resolver)?.toInt()
        val newValue = action.value.value.evaluate(resolver)
        view.setVariable(variableName) { variable: Variable.ArrayVariable ->
            variable.apply {
                when (val indexToInsert = index ?: variable.length()) {
                    in 0..length() -> mutate { add(indexToInsert, newValue) }
                    else -> logIndexOutOfBounds(indexToInsert, view)
                }
            }
        }
    }

    private fun handleRemove(action: DivActionTyped.ArrayRemoveValue, view: Div2View, resolver: ExpressionResolver) {
        val variableName = action.value.variableName.evaluate(resolver)
        val index = action.value.index.evaluate(resolver).toInt()
        view.setVariable(variableName) { variable: Variable.ArrayVariable ->
            variable.apply {
                when (index) {
                    in 0 until length() -> mutate { removeAt(index) }
                    else -> logIndexOutOfBounds(index, view)
                }
            }
        }
    }

    private fun Variable.ArrayVariable.mutate(
        action: MutableList<Any>.() -> Unit
    ): Variable.ArrayVariable {
        return apply {
            val newArray = (getValue() as JSONArray)
                .asList<Any>()
                .toMutableList()
                .apply(action::invoke)
                .let(::JSONArray)
            set(newArray)
        }
    }

    private fun Variable.ArrayVariable.length() = (getValue() as JSONArray).length()

    private fun Variable.ArrayVariable.logIndexOutOfBounds(index: Int, div2View: Div2View) {
        div2View.logError(IndexOutOfBoundsException(
            "Index out of bound ($index) for mutation ${this.name} (${length()})"
        ))
    }
}
