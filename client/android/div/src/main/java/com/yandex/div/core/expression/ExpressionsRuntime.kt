package com.yandex.div.core.expression

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.json.expressions.ExpressionResolver

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolver,
    val variableController: VariableController,
    val triggersController: TriggersController,
) {

    fun clearBinding() {
        triggersController.clearBinding()
        variableController.removeGlobalObservers()
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        triggersController.onAttachedToWindow(view)
    }
}
