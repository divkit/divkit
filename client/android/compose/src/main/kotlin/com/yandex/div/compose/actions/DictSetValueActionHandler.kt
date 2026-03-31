package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.data.Variable
import com.yandex.div.internal.util.clone
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div2.DivActionDictSetValue
import org.json.JSONObject
import javax.inject.Inject

internal class DictSetValueActionHandler @Inject constructor(
    private val reporter: DivReporter
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionDictSetValue
    ) {
        val expressionResolver = context.expressionResolver
        val variableName = action.variableName.evaluate(expressionResolver)
        val variable = expressionResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return
        }

        val dictVariable = variable as? Variable.DictVariable
        val dict = dictVariable?.getValue() as? JSONObject
        if (dict == null) {
            reporter.reportError("Variable is not a dict variable: $variableName")
            return
        }

        val key = action.key.evaluate(expressionResolver)
        val newValue = action.value?.evaluateAsPrimitive(expressionResolver)
        val newDict = dict.clone()
        if (newValue == null) {
            newDict.remove(key)
        } else {
            newDict.put(key, newValue)
        }
        dictVariable.set(newDict)
    }
}
