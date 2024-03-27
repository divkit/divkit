package com.yandex.div.core.expression

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.json.expressions.ExpressionResolver

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolver,
    val variableController: VariableControllerImpl,
    val triggersController: TriggersController,
) {

    fun clearBinding() {
        triggersController.clearBinding()
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        triggersController.onAttachedToWindow(view)
    }

    internal fun cleanup() {
        variableController.cleanup()
        triggersController.clearBinding()
    }
}
