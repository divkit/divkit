package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivVisibilityChangeListener
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.DivActionBeaconSender
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.arrayMap
import com.yandex.div.json.expressions.ExpressionResolver
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
    private val visibilityListeners: List<DivVisibilityChangeListener>,
    private val divActionHandler: DivActionHandler,
    private val divActionBeaconSender: DivActionBeaconSender
) {

    private val appearLogCounters = arrayMap<CompositeLogId, Int>()
    private val disappearLogCounters = arrayMap<CompositeLogId, Int>()

    fun dispatchActions(scope: Div2View, resolver: ExpressionResolver, view: View, actions: Array<DivSightAction>) {
        scope.bulkActions {
            actions.forEach {
                dispatchAction(scope, resolver, view, it)
            }
        }
    }

    fun dispatchAction(scope: Div2View, resolver: ExpressionResolver, view: View, action: DivSightAction) {
        val compositeLogId = compositeLogIdOf(scope, action.logId.evaluate(resolver))
        val counter = countersFor(action).getOrPut(compositeLogId) { 0 }
        KLog.i(TAG) { "visibility action dispatched: id=$compositeLogId, counter=$counter" }

        val logLimit = action.logLimit.evaluate(resolver)
        if (logLimit == LIMITLESS_LOG || counter < logLimit) {
            if (divActionHandler.useActionUid) {
                val uuid = UUID.randomUUID().toString()

                // try to handle scheme if it is known, otherwise log
                val handled = scope.actionHandler?.handleAction(action, scope, resolver, uuid) ?: false
                if (!handled && !divActionHandler.handleAction(action, scope, resolver, uuid)) {
                    logAction(scope, resolver, view, action, uuid)
                }
            } else {
                // try to handle scheme if it is known, otherwise log
                val handled = scope.actionHandler?.handleAction(action, scope, resolver) ?: false
                if (!handled && !divActionHandler.handleAction(action, scope, resolver)) {
                    logAction(scope, resolver, view, action)
                }
            }

            countersFor(action)[compositeLogId] = counter + 1
            KLog.i(TAG) { "visibility action logged: $compositeLogId" }
        }
    }

    private fun countersFor(action: DivSightAction): MutableMap<CompositeLogId, Int> {
        return if (action is DivVisibilityAction) {
            appearLogCounters
        } else {
            disappearLogCounters
        }
    }

    private fun logAction(scope: Div2View, resolver: ExpressionResolver, view: View, action: DivSightAction) {
        if (action is DivVisibilityAction) {
            logger.logViewShown(scope, resolver, view, action)
        } else {
            logger.logViewDisappeared(scope, resolver, view, action as DivDisappearAction)
        }
        divActionBeaconSender.sendVisibilityActionBeacon(action, resolver)
    }

    private fun logAction(
        scope: Div2View,
        resolver: ExpressionResolver,
        view: View,
        action: DivSightAction,
        actionUid: String
    ) {
        if (action is DivVisibilityAction) {
            logger.logViewShown(scope, resolver, view, action, actionUid)
        } else {
            logger.logViewDisappeared(scope, resolver, view, action as DivDisappearAction, actionUid)
        }
        divActionBeaconSender.sendVisibilityActionBeacon(action, resolver)
    }

    fun dispatchVisibleViewsChanged(visibleViews: Map<View, Div>) {
        visibilityListeners.forEach {
            it.onViewsVisibilityChanged(visibleViews)
        }
    }

    fun reset(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            appearLogCounters.clear()
            disappearLogCounters.clear()
        } else {
            tags.forEach { tag ->
                appearLogCounters.keys.removeAll { compositeLogId ->
                    compositeLogId.dataTag == tag.id
                }
                disappearLogCounters.keys.removeAll { compositeLogId ->
                    compositeLogId.dataTag == tag.id
                }
            }
        }
    }

    private companion object {
        private const val TAG = "DivVisibilityActionDispatcher"
        private const val LIMITLESS_LOG = 0L
    }
}
