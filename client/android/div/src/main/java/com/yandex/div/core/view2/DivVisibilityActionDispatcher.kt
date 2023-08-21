package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivVisibilityChangeListener
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.DivActionBeaconSender
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.arrayMap
import com.yandex.div2.Div
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import java.util.UUID
import javax.inject.Inject

@DivScope
@Mockable
internal class DivVisibilityActionDispatcher @Inject constructor(
    private val logger: Div2Logger,
    private val visibilityListener: DivVisibilityChangeListener,
    private val divActionHandler: DivActionHandler,
    private val divActionBeaconSender: DivActionBeaconSender
) {

    private val actionLogCounters = arrayMap<CompositeLogId, Int>()

    fun dispatchActions(scope: Div2View, view: View, actions: Array<DivSightAction>) {
        scope.bulkActions {
            actions.forEach {
                dispatchAction(scope, view, it)
            }
        }
    }

    fun dispatchAction(scope: Div2View, view: View, action: DivSightAction) {
        val compositeLogId = compositeLogIdOf(scope, action)
        val counter = actionLogCounters.getOrPut(compositeLogId) { 0 }

        val logLimit = action.logLimit.evaluate(scope.expressionResolver)
        if (logLimit == LIMITLESS_LOG || counter < logLimit) {
            if (divActionHandler.useActionUid == true) {
                val uuid = UUID.randomUUID().toString()

                // try to handle scheme if it is known, otherwise log
                val handled = scope.actionHandler?.handleAction(action, scope, uuid) ?: false
                if (!handled && !divActionHandler.handleAction(action, scope, uuid)) {
                    logAction(scope, view, action, uuid)
                }
            } else {
                // try to handle scheme if it is known, otherwise log
                val handled = scope.actionHandler?.handleAction(action, scope) ?: false
                if (!handled && !divActionHandler.handleAction(action, scope)) {
                    logAction(scope, view, action)
                }
            }

            actionLogCounters[compositeLogId] = counter + 1
            KLog.d(TAG) { "visibility action logged: $compositeLogId" }
        }
    }

    private fun logAction(scope: Div2View, view: View, action: DivSightAction) {
        if (action is DivVisibilityAction) {
            logger.logViewShown(scope, view, action)
        } else {
            logger.logViewDisappeared(scope, view, action as DivDisappearAction)
        }
        divActionBeaconSender.sendVisibilityActionBeacon(action, scope.expressionResolver)
    }

    private fun logAction(scope: Div2View, view: View, action: DivSightAction, actionUid: String) {
        if (action is DivVisibilityAction) {
            logger.logViewShown(scope, view, action, actionUid)
        } else {
            logger.logViewDisappeared(scope, view, action as DivDisappearAction, actionUid)
        }
        divActionBeaconSender.sendVisibilityActionBeacon(action, scope.expressionResolver)
    }

    fun dispatchVisibleViewsChanged(visibleViews: Map<View, Div>) {
        visibilityListener.onViewsVisibilityChanged(visibleViews)
    }

    fun reset() {
        actionLogCounters.clear()
    }

    private companion object {
        private const val TAG = "DivVisibilityActionDispatcher"
        private const val LIMITLESS_LOG = 0L
    }
}
