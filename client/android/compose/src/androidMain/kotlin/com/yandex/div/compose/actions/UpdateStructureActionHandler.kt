package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.data.Variable
import com.yandex.div.internal.actions.UpdateStructureHelper
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div2.DivActionUpdateStructure
import dev.zacsweers.metro.Inject

@Inject
internal class UpdateStructureActionHandler(
    private val reporter: DivReporter
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionUpdateStructure,
        scopeId: String?,
    ) {
        val expressionResolver = context.originExpressionResolver
        val variableName = action.variableName.evaluate(expressionResolver)
        val scopeResolver = context.findScopeResolver(scopeId) ?: return
        val variable = scopeResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return
        }

        val helper = UpdateStructureHelper(
            reportError = { reporter.reportError(it) }
        )
        val path = action.path.evaluate(expressionResolver)
        val newValue = action.value.evaluateAsPrimitive(expressionResolver)
        when (variable) {
            is Variable.ArrayVariable -> helper.updateArrayStructure(variable, path, newValue)
            is Variable.DictVariable -> helper.updateDictStructure(variable, path, newValue)
            else -> reporter.reportError("Action requires array or dictionary variable")
        }
    }
}
