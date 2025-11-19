package com.yandex.div.core.view2

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.core.os.postDelayed
import androidx.core.view.children
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.SynchronizedWeakHashMap
import com.yandex.div.core.util.allAppearActions
import com.yandex.div.core.util.allDisappearActions
import com.yandex.div.core.util.doOnHierarchyLayout
import com.yandex.div.core.view2.divs.bindingContext
import com.yandex.div.core.view2.divs.duration
import com.yandex.div.internal.Assert
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.KLog
import com.yandex.div.json.expressions.ExpressionResolver
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
    private val appearTrackedTokens = DivVisibilityTokenHolder()
    private val disappearTrackedTokens = DivVisibilityTokenHolder()

    private val isEnabledObserver = SightActionIsEnabledObserver(
        onEnable = { scope, resolver, view, div, action ->
            when (action) {
                is DivVisibilityAction -> trackVisibilityActions(scope, resolver, view, div, listOf(action), emptyList())
                is DivDisappearAction -> trackVisibilityActions(scope, resolver, view, div, emptyList(), listOf(action))
            }

        },
        onDisable = { scope, resolver, _, _, action ->
            when (action) {
                is DivVisibilityAction -> shouldTrackVisibilityAction(scope, resolver, null, action, 0, appearTrackedTokens)
                is DivDisappearAction -> shouldTrackVisibilityAction(scope, resolver, null, action, 0, disappearTrackedTokens)
            }
        }
    )

    private val visibleActions = WeakHashMap<View, Div>()
    private val enqueuedVisibilityActions = WeakHashMap<View, Div>()
    private val previousVisibilityIsFull = WeakHashMap<View, Boolean>()

    private val divWithWaitingDisappearActions = SynchronizedWeakHashMap<View, Div>()
    // Actions that was more visible than its disappear trigger percent, so they can be triggered for disappearing
    private val appearedForDisappearActions = WeakHashMap<View, MutableSet<DivDisappearAction>>()

    private var hasPostedUpdateVisibilityTask = false
    private val updateVisibilityTask = Runnable {
        visibilityActionDispatcher.dispatchVisibleViewsChanged(visibleActions)
        hasPostedUpdateVisibilityTask = false
    }

    fun getDivWithWaitingDisappearActions() = divWithWaitingDisappearActions.createMap()

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
        resolver: ExpressionResolver,
        view: View?,
        div: Div,
        appearActions: List<DivVisibilityAction> = div.value().allAppearActions,
        disappearActions: List<DivDisappearAction> = div.value().allDisappearActions
    ) {
        val visibilityActions = appearActions + disappearActions
        if (visibilityActions.isEmpty()) return

        val originalDataTag = scope.dataTag
        if (view != null) {
            if (enqueuedVisibilityActions.containsKey(view)) return

            view.doOnHierarchyLayout(
                action = {
                    // Prevent visibility tracking when data has changed
                    if (scope.dataTag == originalDataTag) {
                        isEnabledObserver.observe(view, scope, resolver, div, visibilityActions)
                        trackVisibilityActions(
                            scope,
                            resolver,
                            view,
                            div,
                            appearActions.filterEnabled(resolver),
                            disappearActions.filterEnabled(resolver)
                        )
                    }

                    enqueuedVisibilityActions.remove(view)
                },
                onEnqueuedAction = {
                    enqueuedVisibilityActions[view] = div
                }
            )
        } else {
            // Canceling tracking
            isEnabledObserver.cancelObserving(visibilityActions)
            appearActions.forEach { action ->
                shouldTrackVisibilityAction(scope, resolver, null, action, 0, appearTrackedTokens)
            }
            disappearActions.forEach { action ->
                shouldTrackVisibilityAction(scope, resolver, null, action, 0, disappearTrackedTokens)
            }
        }
    }

    fun trackDetachedView(
        context: BindingContext,
        view: View,
        div: Div
    ) {
        val actions = div.value().disappearActions ?: return
        val resolver = context.expressionResolver
        trackVisibilityActions(
            context.divView,
            resolver,
            view,
            div,
            emptyList(),
            actions.filterEnabled(resolver)
        )
    }

    fun startTrackingViewsHierarchy(context: BindingContext, root: View, rootDiv: Div?) {
        trackViewsHierarchy(context, root, rootDiv) { currentView, currentDiv ->
            val isViewFullyVisible = viewVisibilityCalculator.isViewFullyVisible(currentView)
            if (isViewFullyVisible && previousVisibilityIsFull[currentView] == true) {
                false
            } else {
                previousVisibilityIsFull[currentView] = isViewFullyVisible
                currentDiv?.let {
                    val ctx = currentView.bindingContext ?: context
                    trackVisibilityActionsOf(context.divView, ctx.expressionResolver, currentView, it)
                }
                true
            }
        }
    }

    fun cancelTrackingViewsHierarchy(context: BindingContext, root: View, div: Div?) {
        trackViewsHierarchy(context, root, div) { currentView, currentDiv ->
            previousVisibilityIsFull.remove(currentView)
            currentDiv?.let {
                val ctx = currentView.bindingContext ?: context
                trackVisibilityActionsOf(context.divView, ctx.expressionResolver, null, it)
            }
            true
        }
    }

    private fun trackViewsHierarchy(
        context: BindingContext,
        view: View,
        div: Div?,
        trackAction: (View, Div?) -> Boolean
    ) {
        if (!trackAction(view, div) || view !is ViewGroup) {
            return
        }
        view.children.forEach {
            val childDiv = context.divView.takeBindingDiv(it)
            trackViewsHierarchy(context, it, childDiv, trackAction)
        }
    }

    private fun trackVisibilityActions(
        scope: Div2View,
        resolver: ExpressionResolver,
        view: View,
        div: Div,
        appearActions: List<DivVisibilityAction>,
        disappearActions: List<DivDisappearAction>
    ) {
        Assert.assertMainThread()

        val visibilityPercentage = view.let {
            val result = viewVisibilityCalculator.calculateVisibilityPercentage(view)
            updateVisibility(view, div, result)
            result
        }

        appearActions.groupBy { action ->
            action.duration.evaluate(resolver)
        }.forEach { (delayMs, actions) ->
            val actionsToTrack = actions.filterTo(ArrayList(actions.size)) { action ->
                shouldTrackVisibilityAction(scope, resolver, view, action, visibilityPercentage, appearTrackedTokens)
            }
            if (actionsToTrack.isNotEmpty()) {
                startTracking(scope, resolver, view, actionsToTrack, delayMs, appearTrackedTokens)
            }
        }

        disappearActions.groupBy { action ->
            action.duration.evaluate(resolver)
        }.forEach { (delayMs, actions) ->
            var haveWaitingDisappearActions = false
            actions.forEach {
                val actionVisibilityPercentage = it.visibilityPercentage.evaluate(resolver)
                val needWaiting = visibilityPercentage > actionVisibilityPercentage
                haveWaitingDisappearActions = haveWaitingDisappearActions || needWaiting
                if (needWaiting) {
                    appearedForDisappearActions.getOrPut(view) { mutableSetOf() }.apply { add(it) }
                }
            }
            if (haveWaitingDisappearActions) {
                divWithWaitingDisappearActions[view] = div
            }

            val actionsToTrack = actions.filterTo(ArrayList(actions.size)) { action ->
                shouldTrackVisibilityAction(scope, resolver, view, action, visibilityPercentage, disappearTrackedTokens)
            }
            if (actionsToTrack.isNotEmpty()) {
                startTracking(scope, resolver, view, actionsToTrack, delayMs, disappearTrackedTokens)
            }
        }
    }

    /**
     * @return true for action to track, false otherwise.
     */
    private fun shouldTrackVisibilityAction(
        scope: Div2View,
        resolver: ExpressionResolver,
        view: View?,
        action: DivSightAction,
        visibilityPercentage: Int,
        trackedTokens: DivVisibilityTokenHolder
    ): Boolean {
        val trackable = when (action) {
            is DivVisibilityAction -> {
                visibilityPercentage >= action.visibilityPercentage.evaluate(resolver)
            }
            is DivDisappearAction -> {
                (appearedForDisappearActions[view]?.contains(action) ?: false) &&
                    visibilityPercentage <= action.visibilityPercentage.evaluate(resolver)
            }
            else -> {
                KAssert.fail { "Trying to check visibility for class without known visibility range" }
                false
            }
        }

        val compositeLogId = compositeLogIdOf(scope, action.logId.evaluate(resolver))
        // We are using the original instance of compositeLogId that was placed in 'trackedActionIds' previously
        // so that it can pass reference equality check in handler message queue
        val originalLogId = trackedTokens.getLogId(compositeLogId)

        when {
            view != null && originalLogId == null && trackable -> return true
            view != null && originalLogId == null && !trackable -> Unit
            view != null && originalLogId != null && trackable -> Unit
            view != null && originalLogId != null && !trackable -> cancelTracking(originalLogId, view, action, trackedTokens)
            view == null && originalLogId != null -> cancelTracking(originalLogId, null, action, trackedTokens)
            view == null && originalLogId == null -> Unit
        }
        return false
    }

    private fun startTracking(
        scope: Div2View,
        resolver: ExpressionResolver,
        view: View,
        actions: List<DivSightAction>,
        delayMs: Long,
        trackedTokens: DivVisibilityTokenHolder
    ) {
        val logIds = actions.associateTo(HashMap(actions.size, 1f)) { action ->
            val compositeLogId = compositeLogIdOf(scope, action.logId.evaluate(resolver))
            KLog.i(TAG) { "startTracking: id=$compositeLogId" }
            return@associateTo compositeLogId to action
        }.let { Collections.synchronizedMap(it) }
        trackedTokens.add(logIds)
        val originalDataLogId = scope.logId
        /* We use map of CompositeLogId to DivSightAction as token here, so we can cancel
         * individual actions while still execute the rest of it as a bulk. */
        handler.postDelayed(delayInMillis = delayMs, token = logIds) {
            KLog.i(TAG) { "dispatchActions: id=${logIds.keys.joinToString()}" }
            appearedForDisappearActions[view]?.let { waitingActions ->
                actions.filterIsInstance<DivDisappearAction>().forEach(waitingActions::remove)
                if (waitingActions.isEmpty()) {
                    appearedForDisappearActions.remove(view)
                    divWithWaitingDisappearActions.remove(view)
                }
            }
            if (scope.logId == originalDataLogId) {
                visibilityActionDispatcher.dispatchActions(scope, resolver, view, logIds.values.toTypedArray())
            }
        }
    }

    private fun cancelTracking(
        compositeLogId: CompositeLogId,
        view: View?,
        action: DivSightAction,
        trackedTokens: DivVisibilityTokenHolder
    ) {
        KLog.i(TAG) { "cancelTracking: id=$compositeLogId" }
        trackedTokens.remove(compositeLogId) { emptyToken ->
            handler.removeCallbacksAndMessages(emptyToken)
        }
        val waitingActions = appearedForDisappearActions[view]
        if (action is DivDisappearAction && view != null && waitingActions != null) {
            waitingActions.remove(action)
            if (waitingActions.isEmpty()) {
                appearedForDisappearActions.remove(view)
                divWithWaitingDisappearActions.remove(view)
            }
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

    private fun <T : DivSightAction> List<T>.filterEnabled(resolver: ExpressionResolver) =
        filter { action -> action.isEnabled.evaluate(resolver) }

    private companion object {
        const val TAG = "DivVisibilityActionTracker"
    }
}
