package com.yandex.div.core.expression

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.triggers.TriggersController

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolverImpl,
    val triggersController: TriggersController? = null,
) {
    private var unsubscribed = true

    val variableController = expressionResolver.variableController
    val runtimeStore = expressionResolver.runtimeStore

    fun clearBinding(view: DivViewFacade) {
        triggersController?.clearBinding(view)
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        triggersController?.onAttachedToWindow(view)
    }

    fun updateSubscriptions() {
        if (unsubscribed) {
            unsubscribed = false
            expressionResolver.subscribeOnVariables()
            variableController.restoreSubscriptions()
        }
    }

    internal fun cleanup(divView: DivViewFacade?) {
        if (!unsubscribed) {
            unsubscribed = true
            triggersController?.clearBinding(divView)
            variableController.cleanupSubscriptions()
        }
    }
}
