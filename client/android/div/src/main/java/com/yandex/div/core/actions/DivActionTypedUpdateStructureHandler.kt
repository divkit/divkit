package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
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
internal class DivActionTypedUpdateStructureHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when (action) {
        is DivActionTyped.UpdateStructure -> {
            handleAction(action.value, view, resolver)
            true
        }

        else -> false
    }

    private fun handleAction(
        action: DivActionUpdateStructure,
        divView: Div2View,
        resolver: ExpressionResolver
    ) {
        val helper = UpdateStructureHelper(
            reportError = { divView.logError(RuntimeException(it)) }
        )
        val variableName = action.variableName.evaluate(resolver)
        VariableMutationHandler.setVariable(divView, variableName, resolver) { variable: Variable ->
            val path = action.path.evaluate(resolver)
            val newValue = action.value.evaluateAsPrimitive(resolver)
            when (variable) {
                is Variable.ArrayVariable ->
                    helper.updateArrayStructure(variable, path, newValue)

                is Variable.DictVariable ->
                    helper.updateDictStructure(variable, path, newValue)

                else ->
                    divView.logError(RuntimeException("Action requires array or dictionary variable"))
            }
            return@setVariable variable
        }
    }
}
