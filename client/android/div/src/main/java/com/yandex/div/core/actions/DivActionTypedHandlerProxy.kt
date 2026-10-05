package com.yandex.div.core.actions

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.downloader.DivDownloadActionHandler
import com.yandex.div.core.expression.evaluation.JSONObjectEvaluator
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivDownloadCallbacks
import com.yandex.div2.DivSightAction
import org.json.JSONObject

internal object DivActionTypedHandlerProxy {

    @JvmStatic
    fun handleVisibilityAction(action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.scopeId, action.typed, action.payload, view, resolver, action.downloadCallbacks)
    }

    @JvmStatic
    fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return handleAction(action.scopeId, action.typed, action.payload, view, resolver, action.downloadCallbacks)
    }

    private fun handleAction(
        scopeId: Expression<String>?,
        action: DivActionTyped?,
        payload: JSONObject?,
        view: DivViewFacade,
        resolver: ExpressionResolver,
        downloadCallbacks: DivDownloadCallbacks? = null,
    ): Boolean {
        if (action == null) {
            return false
        }
        if (action is DivActionTyped.ExtensionAction) {
            val divView = view as? Div2View ?: return false
            val extensionAction = action.value
            return divView.div2Component.extensionController.handleAction(
                divView = divView,
                extensionId = extensionAction.extensionId.evaluate(resolver),
                divId = extensionAction.divId.evaluate(resolver),
                scopeId = scopeId?.evaluate(resolver),
                payload = payload?.let { JSONObjectEvaluator(it, resolver, divView::logError) },
                resolver = resolver,
            )
        }
        if (view !is Div2View) {
            Assert.fail("Div2View should be used!")
            return false
        }
        if (action is DivActionTyped.Download) {
            return DivDownloadActionHandler.handleAction(action.value, downloadCallbacks, view, resolver)
        }
        val scope = scopeId?.evaluate(resolver)
        return view.div2Component.actionTypedHandlerCombiner.handleAction(scope, action, view, resolver)
    }
}
