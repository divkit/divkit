package com.yandex.divkit.demo.div

import android.view.View
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivGallery
import com.yandex.div2.DivPager
import com.yandex.div2.DivPatch
import com.yandex.div2.DivVisibilityAction
import com.yandex.divkit.regression.ScenarioLogDelegate

class DemoDiv2Logger(
    private val scenarioLogDelegate: ScenarioLogDelegate = ScenarioLogDelegate.Stub
) : Div2Logger {

    companion object {
        private const val TAG = "DemoDiv2Logger"

        private val capturedVisibilityActions = mutableListOf<DivVisibilityAction>()
        private var captureVisibilityActions = false
        private val capturedLogActions = mutableListOf<String>()
        val logActions get() = ArrayList(capturedLogActions)

        fun withCaptureVisibilityActions(block: () -> Unit): List<DivVisibilityAction> {
            return try {
                captureVisibilityActions = true
                capturedVisibilityActions.clear()
                block()
                ArrayList(capturedVisibilityActions)
            } finally {
                captureVisibilityActions = false
                capturedVisibilityActions.clear()
            }
        }

        fun clearLogActions() = capturedLogActions.clear()
    }

    override fun logClick(divView: Div2View, resolver: ExpressionResolver, view: View, action: DivAction) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logClick(cardId = ${divView.logId}, id = $actionLogId)"
        }
    }

    override fun logLongClick(divView: Div2View, resolver: ExpressionResolver, view: View, action: DivAction) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logLongClick(cardId = ${divView.logId}, id = $actionLogId)"
        }
    }

    override fun logDoubleClick(divView: Div2View, resolver: ExpressionResolver, view: View, action: DivAction) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logDoubleClick(cardId = ${divView.logId}, id = $actionLogId)"
        }
    }

    override fun logFocusChanged(divView: Div2View, resolver: ExpressionResolver, view: View, action: DivAction, haveFocus: Boolean) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logFocusChanged(cardId = ${divView.logId}, id = $actionLogId, haveFocus = $haveFocus)"
        }
    }

    override fun logHoverChanged(
        divView: Div2View,
        resolver: ExpressionResolver,
        view: View,
        action: DivAction,
        isHovered: Boolean
    ) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logHoverChanged(cardId = ${divView.logId}, id = $actionLogId, isHovered = $isHovered)"
        }
    }

    override fun logPressChanged(
        divView: Div2View,
        resolver: ExpressionResolver,
        view: View,
        action: DivAction,
        isPressed: Boolean
    ) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logPressChanged(cardId = ${divView.logId}, id = $actionLogId, isPressed = $isPressed)"
        }
    }

    override fun logTabPageChanged(divView: Div2View, selectedTab: Int) {
        log {
            "logTabPageChanged(cardId = ${divView.logId}, selectedTab = $selectedTab)"
        }
    }

    override fun logTabTitlesScroll(divView: Div2View) {
        log {
            "logTabTitlesScroll(cardId = ${divView.logId})"
        }
    }

    override fun logGalleryScroll(divView: Div2View) {
        log {
            "logGalleryScroll(cardId = ${divView.logId})"
        }
    }

    override fun logGalleryCompleteScroll(
        divView: Div2View,
        resolver: ExpressionResolver,
        divGallery: DivGallery,
        firstVisibleItem: Int,
        lastVisibleItem: Int,
        scrollDirection: String
    ) {
        log {
            "logGalleryCompleteScroll(cardId = ${divView.logId}, firstVisibleItem = $firstVisibleItem, lastVisibleItem = $lastVisibleItem, scrollDirection = $scrollDirection)"
        }
    }

    override fun logPagerChangePage(
        divView: Div2View,
        resolver: ExpressionResolver,
        divPager: DivPager,
        currentPageIndex: Int,
        scrollDirection: String
    ) {
        log {
            "logPagerChangePage(cardId = ${divView.logId}, currentPageIndex = $currentPageIndex), scrollDirection = $scrollDirection)"
        }
    }

    override fun logViewShown(
        divView: Div2View,
        resolver: ExpressionResolver,
        view: View,
        action: DivVisibilityAction,
    ) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logViewShown(cardId = ${divView.logId}, id = $actionLogId, url = ${action.url?.evaluate(resolver)}), referer = ${action.referer?.evaluate(resolver)})"
        }
        if (captureVisibilityActions) {
            capturedVisibilityActions += action
        }
    }

    override fun logViewDisappeared(
        divView: Div2View,
        resolver: ExpressionResolver,
        view: View,
        action: DivDisappearAction,
    ) {
        val actionLogId = action.logId.evaluate(resolver)
        log {
            "logViewDisappeared(cardId = ${divView.logId}, id = $actionLogId, url = ${action.url?.evaluate(resolver)}), referer = ${action.referer?.evaluate(resolver)})"
        }
    }

    override fun logTrigger(divView: Div2View, action: DivAction) {
        val actionLogId = action.logId.evaluate(divView.expressionResolver)
        log {
            "logTrigger(cardId = ${divView.logId}, id = $actionLogId)"
        }
    }

    override fun logBindingResult(
        divView: Div2View,
        oldData: DivData?,
        newData: DivData?,
        result: String,
        eventsMessage: String?,
    ) {
        log {
            val builder = StringBuilder("logBindingResult(cardId = ${divView.logId}, result = $result")
            eventsMessage?.let {
                builder.append(", events = $it")
            }
            builder.append(")")
            builder.toString()
        }
    }

    override fun logPatchResult(
        divView: Div2View,
        patch: DivPatch,
        result: String,
        eventsMessage: String?,
    ) {
        log {
            val builder = StringBuilder("logPatchResult(result = $result")
            eventsMessage?.let {
                builder.append(", events = $it")
            }
            builder.append(")")
            builder.toString()
        }
    }

    override fun logImeEnter(
        divView: Div2View,
        resolver: ExpressionResolver,
        inputView: View,
        action: DivAction
    ) {
        log {
            val actionLogId = action.logId.evaluate(divView.expressionResolver)
            "logImeEnter(cardId = ${divView.logId}, id = $actionLogId)"
        }
    }

    private inline fun log(message : () -> String) {
        KLog.d(TAG, message)
        capturedLogActions.add(message.invoke())
        scenarioLogDelegate.println(message.invoke())
    }
}
