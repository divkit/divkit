package com.yandex.div.core.view2

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.AnyThread
import androidx.core.os.postDelayed
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.doOnHierarchyLayout
import com.yandex.div.core.view2.divs.allSightActions
import com.yandex.div.core.view2.divs.duration
import com.yandex.div.internal.Assert
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.KLog
import com.yandex.div2.Div
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import java.util.Collections
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
    private val enqueuedVisibilityActions = WeakHashMap<View, Div>()

    // Actions that was more visible than its disappear trigger percent, so they can be triggered for disappearing
    private val appearedForDisappearActions = WeakHashMap<View, Div>()

    private var hasPostedUpdateVisibilityTask = false
    private val updateVisibilityTask = Runnable {
        visibilityActionDispatcher.dispatchVisibleViewsChanged(visibleActions)
        hasPostedUpdateVisibilityTask = false
    }

    fun getActionsWaitingForDisappear() = appearedForDisappearActions.toMap()

    @AnyThread
    fun updateVisibleViews(viewList: List<View>) {
        val visibleIterator = visibleActions.iterator()
        while (visibleIterator.hasNext()) {
            if (visibleIterator.next().key !in viewList) visibleIterator.remove()
        }

        if (!hasPostedUpdateVisibilityTask) {
            hasPostedUpdateVisibilityTask = true
            handler.post(updateVisibilityTask)
        }
    }

    @AnyThread
    fun trackVisibilityActionsOf(
        scope: Div2View,
        view: View?,
        div: Div,
        visibilityActions: List<DivSightAction> = div.value().allSightActions
    ) {
        if (visibilityActions.isEmpty()) return

        val originalDivData = scope.divData
        if (view != null) {
            if (enqueuedVisibilityActions.containsKey(view)) return

            view.doOnHierarchyLayout(
                action = {
                    // Prevent visibility tracking when data has changed
                    if (scope.divData == originalDivData) {
                        trackVisibilityActions(scope, view, div, visibilityActions)
                    }

                    enqueuedVisibilityActions.remove(view)
                },
                onEnqueuedAction = {
                    enqueuedVisibilityActions[view] = div
                }
            )
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
        visibilityActions: List<DivSightAction>
    ) {
        Assert.assertMainThread()

        val visibilityPercentage = view.let {
            val result = viewVisibilityCalculator.calculateVisibilityPercentage(view)
            updateVisibility(view, div, result)
            result
        }

        var viewAppearedForDisappear = appearedForDisappearActions.containsKey(view)

        visibilityActions.groupBy { action: DivSightAction ->
            action.duration.evaluate(scope.expressionResolver)
        }.forEach { entry: Map.Entry<Long, List<DivSightAction>> ->
            val (delayMs, actions) = entry

            if (!viewAppearedForDisappear &&
                actions.any {
                    it is DivDisappearAction &&
                        visibilityPercentage > it.visibilityPercentage.evaluate(scope.expressionResolver)
                }
            ) {
                appearedForDisappearActions[view] = div
                viewAppearedForDisappear = true
            }

            val actionsToBeTracked = actions.filterTo(ArrayList(actions.size)) { action ->
                shouldTrackVisibilityAction(scope, view, action, visibilityPercentage)
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
        action: DivSightAction,
        visibilityPercentage: Int
    ): Boolean {
        val trackable = when (action) {
            is DivVisibilityAction -> {
                visibilityPercentage >= action.visibilityPercentage.evaluate(scope.expressionResolver)
            }
            is DivDisappearAction -> {
                appearedForDisappearActions.containsKey(view) &&
                    visibilityPercentage <= action.visibilityPercentage.evaluate(scope.expressionResolver)
            }
            else -> {
                KAssert.fail { "Trying to check visibility for class without known visibility range" }
                false
            }
        }

        val compositeLogId = compositeLogIdOf(scope, action)
        // We are using the original instance of compositeLogId that was placed in 'trackedActionIds' previously
        // so that it can pass reference equality check in handler message queue
        val originalLogId = trackedTokens.getLogId(compositeLogId)

        when {
            view != null && originalLogId == null && trackable -> return true
            view != null && originalLogId == null && !trackable -> Unit
            view != null && originalLogId != null && trackable -> Unit
            view != null && originalLogId != null && !trackable -> cancelTracking(originalLogId, view, action)
            view == null && originalLogId != null -> cancelTracking(originalLogId, null, action)
            view == null && originalLogId == null -> Unit
        }
        return false
    }

    private fun startTracking(
        scope: Div2View,
        view: View,
        actions: List<DivSightAction>,
        delayMs: Long
    ) {
        val logIds = actions.associateTo(HashMap(actions.size, 1f)) { action ->
            val compositeLogId = compositeLogIdOf(scope, action)
            KLog.e(TAG) { "startTracking: id=$compositeLogId" }
            return@associateTo compositeLogId to action
        }.let { Collections.synchronizedMap(it) }
        trackedTokens.add(logIds)
        appearedForDisappearActions.remove(view)
        /* We use map of CompositeLogId to DivSightAction as token here, so we can cancel
         * individual actions while still execute the rest of it as a bulk. */
        handler.postDelayed(delayInMillis = delayMs, token = logIds) {
            KLog.e(TAG) { "dispatchActions: id=${logIds.keys.joinToString()}" }
            visibilityActionDispatcher.dispatchActions(scope, view, logIds.values.toTypedArray())
        }
    }

    private fun cancelTracking(compositeLogId: CompositeLogId, view: View?, action: DivSightAction) {
        KLog.e(TAG) { "cancelTracking: id=$compositeLogId" }
        trackedTokens.remove(compositeLogId) { emptyToken ->
            handler.removeCallbacksAndMessages(emptyToken)
        }
        if (action is DivDisappearAction && view != null) {
            appearedForDisappearActions.remove(view)
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
