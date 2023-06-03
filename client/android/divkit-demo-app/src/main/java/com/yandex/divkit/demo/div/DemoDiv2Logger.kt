package com.yandex.divkit.demo.div

import android.view.View
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div2.DivAction
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivGallery
import com.yandex.div2.DivPager
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

    override fun logClick(divView: Div2View, view: View, action: DivAction) {
        log(TAG) {
            "logClick(cardId = ${divView.logId}, id = ${action.logId})"
        }
    }

    override fun logLongClick(divView: Div2View, view: View, action: DivAction) {
        log(TAG) {
            "logLongClick(cardId = ${divView.logId}, id = ${action.logId})"
        }
    }

    override fun logDoubleClick(divView: Div2View, view: View, action: DivAction) {
        log(TAG) {
            "logDoubleClick(cardId = ${divView.logId}, id = ${action.logId})"
        }
    }

    override fun logFocusChanged(divView: Div2View, view: View, action: DivAction, haveFocus: Boolean) {
        log(TAG) {
            "logFocusChanged(cardId = ${divView.logId}, id = ${action.logId}, haveFocus = $haveFocus)"
        }
    }

    override fun logTabPageChanged(divView: Div2View, selectedTab: Int) {
        log(TAG) {
            "logTabPageChanged(cardId = ${divView.logId}, selectedTab = $selectedTab)"
        }
    }

    override fun logTabTitlesScroll(divView: Div2View) {
        log(TAG) {
            "logTabTitlesScroll(cardId = ${divView.logId})"
        }
    }

    override fun logGalleryScroll(divView: Div2View) {
        log(TAG) {
            "logGalleryScroll(cardId = ${divView.logId})"
        }
    }

    override fun logGalleryCompleteScroll(
        divView: Div2View,
        divGallery: DivGallery,
        firstVisibleItem: Int,
        lastVisibleItem: Int,
        scrollDirection: String
    ) {
        log(TAG) {
            "logGalleryCompleteScroll(cardId = ${divView.logId}, firstVisibleItem = $firstVisibleItem, lastVisibleItem = $lastVisibleItem, scrollDirection = $scrollDirection)"
        }
    }

    override fun logPagerChangePage(
        divView: Div2View,
        divPager: DivPager,
        currentPageIndex: Int,
        scrollDirection: String
    ) {
        log(TAG) {
            "logPagerChangePage(cardId = ${divView.logId}, currentPageIndex = $currentPageIndex), scrollDirection = $scrollDirection)"
        }
    }

    override fun logViewShown(divView: Div2View, view: View, action: DivVisibilityAction) {
        val expressionResolver = divView.expressionResolver
        log(TAG) {
            "logViewShown(cardId = ${divView.logId}, id = ${action.logId}, url = ${action.url?.evaluate(expressionResolver)}), referer = ${action.referer?.evaluate(expressionResolver)})"
        }
        if (captureVisibilityActions) {
            capturedVisibilityActions += action
        }
    }

    override fun logViewDisappeared(divView: Div2View, view: View, action: DivDisappearAction) {
        val expressionResolver = divView.expressionResolver
        log(TAG) {
            "logViewDisappeared(cardId = ${divView.logId}, id = ${action.logId}, url = ${action.url?.evaluate(expressionResolver)}), referer = ${action.referer?.evaluate(expressionResolver)})"
        }
    }

    override fun logTrigger(divView: Div2View, action: DivAction) {
        log(TAG) {
            "logTrigger(cardId = ${divView.logId}, id = ${action.logId})"
        }
    }

    private inline fun log(tag: String, message : () -> String) {
        KLog.d(tag, message)
        capturedLogActions.add(message.invoke())
        scenarioLogDelegate.println(message.invoke())
    }
}
