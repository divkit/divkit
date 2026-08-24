package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.internal.actions.UpdateStructureHelper
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.variables.evaluateAsPrimitive
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionUpdateStructure
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedUpdateStructureHandler @Inject constructor(
    private val variableMutationHandler: VariableMutationHandler,
) : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when (action) {
        is DivActionTyped.UpdateStructure -> {
            handleAction(action.value, resolver, view.errorCollector)
            true
        }

        else -> false
    }

    private fun handleAction(
        action: DivActionUpdateStructure,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val variableName = action.variableName.evaluate(resolver)
        variableMutationHandler.setVariable(variableName, resolver, errorCollector) { variable: Variable ->
            variable.also { it.mutate(action, resolver, errorCollector) }
        }
    }

    private fun Variable.mutate(
        action: DivActionUpdateStructure,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val helper = UpdateStructureHelper(
            reportError = { errorCollector.logError(RuntimeException(it)) }
        )
        val path = action.path.evaluate(resolver)
        val newValue = action.value.evaluateAsPrimitive(resolver)
        when (this) {
            is Variable.ArrayVariable -> helper.updateArrayStructure(this, path, newValue)

            is Variable.DictVariable -> helper.updateDictStructure(this, path, newValue)

            else -> errorCollector.logError(RuntimeException("Action requires array or dictionary variable"))
        }
    }
}
