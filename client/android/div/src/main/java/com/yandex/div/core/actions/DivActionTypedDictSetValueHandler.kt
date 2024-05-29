package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionDictSetValue
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedDictSetValueHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {
        is DivActionTyped.DictSetValue -> {
            handleSetValue(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleSetValue(
        action: DivActionDictSetValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val key = action.key.evaluate(resolver)
        val newValue = action.value?.evaluate(resolver)
        view.setVariable(variableName) { variable: Variable ->
            if (variable !is Variable.DictVariable) {
                view.logError(
                    IllegalArgumentException("dict_set_value action requires dict variable")
                )
                return@setVariable variable
            }

            val value = variable.getValue() as? JSONObject
            if (value == null) {
                view.logError(IllegalArgumentException("Invalid variable value"))
                return@setVariable variable
            }

            if (newValue == null) {
                value.remove(key)
                variable.set(value)
            } else {
                variable.set(value.put(key, newValue))
            }
            return@setVariable variable
        }
    }
}
