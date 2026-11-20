package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.variables.castAndSetValue
import com.yandex.div.internal.variables.evaluate
import com.yandex.div2.DivActionSetVariable
import dev.zacsweers.metro.Inject

@Inject
internal class SetVariableActionHandler(
    private val reporter: DivReporter
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionSetVariable,
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

        try {
            variable.castAndSetValue(action.value.evaluate(expressionResolver))
        } catch (e: Exception) {
            reporter.reportError(e)
        }
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction.SetVariable,
        scopeId: String?,
    ) {
        val variableName = action.name
        val scopeResolver = context.findScopeResolver(scopeId) ?: return
        val variable = scopeResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return
        }

        try {
            variable.set(action.value)
        } catch (e: Exception) {
            reporter.reportError(e)
        }
    }
}
