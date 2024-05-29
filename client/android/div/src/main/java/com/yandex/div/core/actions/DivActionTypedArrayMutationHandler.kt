package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.asList
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
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
            handleInsert(action.value, view, resolver)
            true
        }

        is DivActionTyped.ArrayRemoveValue -> {
            handleRemove(action.value, view, resolver)
            true
        }

        else -> false
    }

    private fun handleInsert(
        action: DivActionArrayInsertValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index?.evaluate(resolver)?.toInt()
        val newValue = action.value.evaluate(resolver)
        view.setVariable(variableName) { variable: Variable ->
            if (variable !is Variable.ArrayVariable) {
                view.logError(
                    IllegalArgumentException("array_insert_value action requires array variable")
                )
                return@setVariable variable
            }

            val value = variable.getValue() as? JSONArray
            if (value == null) {
                view.logError(IllegalArgumentException("Invalid variable value"))
                return@setVariable variable
            }

            val length = value.length()
            when (val indexToInsert = index ?: length) {
                in 0..length -> variable.set(value.mutate { add(indexToInsert, newValue) })
                else -> view.logError(IndexOutOfBoundsException(
                    "Index out of bound ($indexToInsert) for mutation ${variable.name} (${length})"
                ))
            }
            return@setVariable variable
        }
    }

    private fun handleRemove(
        action: DivActionArrayRemoveValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val index = action.index.evaluate(resolver).toInt()
        view.setVariable(variableName) { variable: Variable ->
            if (variable !is Variable.ArrayVariable) {
                view.logError(
                    IllegalArgumentException("array_remove_value action requires array variable")
                )
                return@setVariable variable
            }

            val value = variable.getValue() as? JSONArray
            if (value == null) {
                view.logError(IllegalArgumentException("Invalid variable value"))
                return@setVariable variable
            }

            val length = value.length()
            when (index) {
                in 0 until length -> variable.set(value.mutate { removeAt(index) })
                else -> view.logError(IndexOutOfBoundsException(
                    "Index out of bound ($index) for mutation ${variable.name} (${length})"
                ))
            }
            return@setVariable variable
        }
    }

    private fun JSONArray.mutate(action: MutableList<Any>.() -> Unit): JSONArray {
        return asList<Any>()
            .toMutableList()
            .apply(action::invoke)
            .let(::JSONArray)
    }
}
