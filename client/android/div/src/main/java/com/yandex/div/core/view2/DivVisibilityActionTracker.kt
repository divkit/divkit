package com.yandex.div.core.view2

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.MainThread
import androidx.core.os.postDelayed
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.KLog
import com.yandex.div.core.util.doOnHierarchyLayout
import com.yandex.div.core.view2.divs.allVisibilityActions
import com.yandex.div2.Div
import com.yandex.div2.DivVisibilityAction
import java.util.WeakHashMap
import javax.inject.Inject

@DivScope
@Mockable
internal class DivVisibilityActionTracker @Inject constructor(
    private val viewVisibilityCalculator: ViewVisibilityCalculator,
    private val visibilityActionDispatcher: DivVisibilityActionDispatcher
) {

    private val handler = Handler(Looper.getMainLooper())
    private val trackedTokens = DivVisibilityTokenHolder()

    private val visibleActions = WeakHashMap<View, Div>()

    private var hasPostedUpdateVisibilityTask = false
    private val updateVisibilityTask = Runnable {
        visibilityActionDispatcher.dispatchVisibleViewsChanged(visibleActions)
        hasPostedUpdateVisibilityTask = false
    }
    @MainThread
    fun trackVisibilityActionsOf(
        scope: Div2View,
        view: View?,
        div: Div,
        visibilityActions: List<DivVisibilityAction> = div.value().allVisibilityActions
    ) {
        if (visibilityActions.isEmpty()) return

        val originalDivData = scope.divData
        if (view != null) {
            view.doOnHierarchyLayout {
                // Prevent visibility tracking when data has changed
                if (scope.divData == originalDivData) {
                    trackVisibilityActions(scope, view, div, visibilityActions)
                }
            }
        } else {
            // Canceling tracking
            visibilityActions.forEach { action ->
                shouldTrackVisibilityAction(scope, view, action, 0)
            }
        }
    }

    private fun trackVisibilityActions(
        scope: Div2View,
        view: View,
        div: Div,
        visibilityActions: List<DivVisibilityAction>
    ) {
        val visibilityPercentage = view.let {
            val result = viewVisibilityCalculator.calculateVisibilityPercentage(view)
            updateVisibility(view, div, result)
            result
        }

        visibilityActions.groupBy { action: DivVisibilityAction ->
            return@groupBy action.visibilityDuration.evaluate(scope.expressionResolver).toLong()
        }.forEach { entry: Map.Entry<Long, List<DivVisibilityAction>> ->
            val (delayMs, actions) = entry
            val actionsToBeTracked = actions.filterTo(ArrayList(actions.size)) { action ->
                return@filterTo shouldTrackVisibilityAction(scope, view, action, visibilityPercentage)
            }
            if (actionsToBeTracked.isNotEmpty()) {
                startTracking(scope, view, actionsToBeTracked, delayMs)
            }
        }
    }

    /**
     * @return true for action to track, false otherwise.
     */
    private fun shouldTrackVisibilityAction(
        scope: Div2View,
        view: View?,
        action: DivVisibilityAction,
        visibilityPercentage: Int
    ): Boolean {
        val visible = visibilityPercentage >= action.visibilityPercentage.evaluate(scope.expressionResolver)
        val compositeLogId = compositeLogIdOf(scope, action)
        // We are using the original instance of compositeLogId that was placed in 'trackedActionIds' previously
        // so that it can pass reference equality check in handler message queue
        val originalLogId = trackedTokens.getLogId(compositeLogId)

        when {
            view != null && originalLogId == null && visible -> return true
            view != null && originalLogId == null && !visible -> Unit
            view != null && originalLogId != null && visible -> Unit
            view != null && originalLogId != null && !visible -> cancelTracking(originalLogId)
            view == null && originalLogId != null -> cancelTracking(originalLogId)
            view == null && originalLogId == null -> Unit
        }
        return false
    }

    private fun startTracking(
        scope: Div2View,
        view: View,
        actions: List<DivVisibilityAction>,
        delayMs: Long
    ) {
        val logIds = actions.associateTo(HashMap(actions.size, 1f)) { action ->
            val compositeLogId = compositeLogIdOf(scope, action)
            KLog.e(TAG) { "startTracking: id=$compositeLogId" }
            return@associateTo compositeLogId to action
        }
        trackedTokens.add(logIds)
        /* We use map of CompositeLogId to DivVisibilityActions as token here, so we can cancel
         * individual actions while still execute the rest of it as a bulk. */
        handler.postDelayed(delayInMillis = delayMs, token = logIds) {
            KLog.e(TAG) { "dispatchActions: id=${logIds.keys.joinToString()}" }
            visibilityActionDispatcher.dispatchActions(scope, view, logIds.values)
        }
    }

    private fun cancelTracking(compositeLogId: CompositeLogId) {
        KLog.e(TAG) { "cancelTracking: id=$compositeLogId" }
        val token: MutableMap<CompositeLogId, DivVisibilityAction> = trackedTokens.getTokenByLogId(compositeLogId) ?: return
        token.remove(compositeLogId)
        if (token.isEmpty()) {
            handler.removeCallbacksAndMessages(token)
            trackedTokens.remove(token)
        }
    }

    private fun updateVisibility(view: View, div: Div, visibilityPercentage: Int) {
        val visible = visibilityPercentage > 0
        if (visible) {
            visibleActions[view] = div
        } else {
            visibleActions.remove(view)
        }

        if (!hasPostedUpdateVisibilityTask) {
            hasPostedUpdateVisibilityTask = true
            handler.post(updateVisibilityTask)
        }
    }

    private companion object {
        const val TAG = "DivVisibilityActionTracker"
    }
}
