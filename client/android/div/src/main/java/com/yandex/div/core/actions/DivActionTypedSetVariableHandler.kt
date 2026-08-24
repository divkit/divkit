package com.yandex.div.core.actions

import com.yandex.div.core.util.ContainerFinder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.variables.castAndSetValue
import com.yandex.div.internal.variables.evaluate
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedSetVariableHandler @Inject constructor(
    private val variableMutationHandler: VariableMutationHandler,
) : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {

        is DivActionTyped.SetVariable -> {
            handleSetVariable(scopeId, action, view, resolver)
            true
        }

        else -> false
    }

    private fun handleSetVariable(
        scopeId: String?,
        action: DivActionTyped.SetVariable,
        view: Div2View,
        actionResolver: ExpressionResolver,
    ) {
        val resolver = scopeId?.let {
            ContainerFinder(scopeId).findContainer(view)?.expressionResolver
        } ?: actionResolver

        val variableName = action.value.variableName.evaluate(resolver)
        val newValue = action.value.value.evaluate(resolver)
        val errorCollector = view.errorCollector
        variableMutationHandler.setVariable(variableName, resolver, errorCollector) { variable: Variable ->
            variable.also { it.mutate(newValue, errorCollector) }
        }
    }

    private fun Variable.mutate(newValue: Any, errorCollector: ErrorCollector) {
        try {
            castAndSetValue(newValue)
        } catch (e: VariableMutationException) {
            errorCollector.logError(e)
        }
    }
}
