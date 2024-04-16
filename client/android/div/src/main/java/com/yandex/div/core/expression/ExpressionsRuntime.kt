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
    private var unsubscribed = true
    private val expressionResolverImpl get() = expressionResolver as? ExpressionResolverImpl
        ?: throw AssertionError("ExpressionRuntime must have ExpressionResolverImpl as expressionResolver.")
    fun clearBinding() {
        triggersController.clearBinding()
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        triggersController.onAttachedToWindow(view)
    }

    fun updateSubscriptions() {
        if (unsubscribed) {
            unsubscribed = false
            variableController.restoreSubscriptions()
            expressionResolverImpl.subscribeOnVariables()
        }
    }

    internal fun cleanup() {
        unsubscribed = true
        variableController.cleanupSubscriptions()
        triggersController.clearBinding()
    }
}
