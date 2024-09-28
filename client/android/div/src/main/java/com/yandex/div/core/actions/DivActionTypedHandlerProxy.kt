package com.yandex.div.core.actions

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.downloader.DivDownloadActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivDownloadCallbacks
import com.yandex.div2.DivSightAction

internal object DivActionTypedHandlerProxy {

    @JvmStatic
    fun handleVisibilityAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.scopeId, action.typed, view, resolver, action.downloadCallbacks)
    }

    @JvmStatic
    fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.scopeId, action.typed, view, resolver, action.downloadCallbacks)
    }

    private fun handleAction(
        scopeId: String?,
        action: DivActionTyped?,
        view: DivViewFacade,
        resolver: ExpressionResolver,
        downloadCallbacks: DivDownloadCallbacks? = null,
    ): Boolean {
        if (action == null) {
            return false
        }
        if (view !is Div2View) {
            Assert.fail("Div2View should be used!")
            return false
        }
        if (action is DivActionTyped.Download) {
            return DivDownloadActionHandler.handleAction(action.value, downloadCallbacks, view, resolver)
        }
        return view.div2Component.actionTypedHandlerCombiner.handleAction(scopeId, action, view, resolver)
    }
}
