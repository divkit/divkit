package com.yandex.div.core.expression

import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.PropertyVariableExecutorImpl
import com.yandex.div.core.view2.Div2View

internal class ExpressionsRuntime(
    val expressionResolver: ExpressionResolverImpl,
    val propertyVariableExecutor: PropertyVariableExecutorImpl? = null,
    val triggersController: TriggersController? = null,
) {
    private var unsubscribed = true

    fun clearBinding(view: Div2View) {
        triggersController?.clearBinding(view)
    }

    fun onAttachedToWindow(view: Div2View) {
        triggersController?.onAttachedToWindow(view)
    }

    fun onDetachedFromWindow(view: Div2View) {
        triggersController?.onDetachedFromWindow(view)
    }

    fun updateSubscriptions() {
        if (unsubscribed) {
            unsubscribed = false
            expressionResolver.subscribeOnVariables()
        }
    }

    internal fun cleanup(divView: Div2View?) {
        if (!unsubscribed) {
            unsubscribed = true
            triggersController?.clearBinding(divView)
            expressionResolver.variableController.cleanupSubscriptions()
        }
    }
}
