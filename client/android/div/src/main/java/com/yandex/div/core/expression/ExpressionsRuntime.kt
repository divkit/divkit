package com.yandex.div.core.expression

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolver,
    val variableController: VariableController,
    val triggersController: TriggersController? = null,
    val runtimeStore: RuntimeStore,
) {
    private val expressionResolverImpl get() = expressionResolver as? ExpressionResolverImpl
    private var unsubscribed = true

    fun clearBinding() {
        triggersController?.clearBinding()
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        triggersController?.onAttachedToWindow(view)
    }

    fun updateSubscriptions() {
        if (unsubscribed) {
            unsubscribed = false
            expressionResolverImpl?.subscribeOnVariables() ?: run {
                Assert.fail("ExpressionRuntime must have ExpressionResolverImpl as expressionResolver.")
            }
            variableController.restoreSubscriptions()
        }
    }

    internal fun cleanup() {
        if (!unsubscribed) {
            unsubscribed = true
            triggersController?.clearBinding()
            variableController.cleanupSubscriptions()
        }
    }
}
