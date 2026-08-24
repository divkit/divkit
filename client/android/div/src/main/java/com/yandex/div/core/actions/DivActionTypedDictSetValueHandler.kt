package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.util.clone
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionDictSetValue
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedDictSetValueHandler @Inject constructor(
    private val variableMutationHandler: VariableMutationHandler,
) : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {
        is DivActionTyped.DictSetValue -> {
            handleSetValue(action.value, resolver, view.errorCollector)
            true
        }
        else -> false
    }

    private fun handleSetValue(
        action: DivActionDictSetValue,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val variableName = action.variableName.evaluate(resolver)
        val key = action.key.evaluate(resolver)
        val newValue = action.value?.evaluateAsPrimitive(resolver)
        variableMutationHandler.setVariable(variableName, resolver, errorCollector) { variable: Variable ->
            variable.also { it.mutate(key, newValue, errorCollector) }
        }
    }

    private fun Variable.mutate(key: String, newValue: Any?, errorCollector: ErrorCollector) {
        val dictVariable = this as? Variable.DictVariable
            ?: return errorCollector.logError(IllegalArgumentException("dict_set_value action requires dict variable"))

        val dict = dictVariable.getValue() as? JSONObject
            ?: return errorCollector.logError(IllegalArgumentException("Invalid variable value"))

        val newDict = dict.clone()
        newValue?.let { return dictVariable.set(newDict.put(key, it)) }

        newDict.remove(key)
        dictVariable.set(newDict)
    }
}
