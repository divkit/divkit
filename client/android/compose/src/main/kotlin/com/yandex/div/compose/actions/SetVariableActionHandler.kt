package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.data.VariableMutationException
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.variables.castAndSetValue
import com.yandex.div.internal.variables.evaluate
import com.yandex.div2.DivActionSetVariable
import javax.inject.Inject

internal class SetVariableActionHandler @Inject constructor(
    private val reporter: DivReporter
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionSetVariable
    ) {
        val expressionResolver = context.expressionResolver
        val variableName = action.variableName.evaluate(expressionResolver)
        val variable = expressionResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return
        }

        try {
            variable.castAndSetValue(action.value.evaluate(expressionResolver))
        } catch (e: VariableMutationException) {
            reporter.reportError(e)
        }
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction.SetVariable
    ) {
        val variableName = action.name
        val variable = context.expressionResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError("Unknown variable: $variableName")
            return
        }

        try {
            variable.set(action.value)
        } catch (e: VariableMutationException) {
            reporter.reportError(e)
        }
    }
}
