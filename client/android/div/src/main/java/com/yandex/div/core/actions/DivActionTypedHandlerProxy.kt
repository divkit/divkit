package com.yandex.div.core.actions

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivSightAction

internal object DivActionTypedHandlerProxy {

    @JvmStatic
    fun handleVisibilityAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.typed, view, resolver)
    }

    @JvmStatic
    fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.typed, view, resolver)
    }

    private fun handleAction(action: DivActionTyped?, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        if (action == null) {
            return false
        }
        if (view !is Div2View) {
            Assert.fail("Div2View should be used!")
            return false
        }
        return view.div2Component.actionTypedHandlerCombiner.handleAction(action, view, resolver)
    }
}
