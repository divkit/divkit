package com.yandex.div.compose.visibility

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.host.LocalDivViewHost
import com.yandex.div.internal.util.duration
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class VisibilityActionTracker @Inject constructor(
    private val actionHandler: DivActionHandler,
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope
) {
    private val pendingActions = mutableMapOf<TrackedAction, Job>()
    private val eligibleDisappearActions = mutableSetOf<TrackedAction>()

    fun isLimitReached(
        context: DivActionHandlingContext,
        action: DivSightAction,
        limit: Int
    ): Boolean = limit > 0 && (context.sightActionCounts[action] ?: 0) >= limit

    fun onVisibilityChanged(
        context: DivActionHandlingContext,
        action: DivVisibilityAction,
        isVisible: Boolean
    ) {
        val trackedAction = TrackedAction(context, action)
        if (!isVisible) {
            cancelAction(trackedAction)
        } else {
            scheduleAction(trackedAction)
        }
    }

    fun onVisibilityChanged(
        context: DivActionHandlingContext,
        action: DivDisappearAction,
        isVisible: Boolean
    ) {
        val trackedAction = TrackedAction(context, action)
        if (isVisible) {
            eligibleDisappearActions.add(trackedAction)
            cancelAction(trackedAction)
        } else {
            scheduleAction(trackedAction)
        }
    }

    fun onContentUnbound() {
        val actions = eligibleDisappearActions.toList()
        eligibleDisappearActions.clear()
        actions.forEach(::cancelAction)
        actions.forEach(::triggerIfNeeded)
    }

    private fun triggerIfNeeded(trackedAction: TrackedAction) {
        val (context, action) = trackedAction
        if (action is DivDisappearAction) {
            eligibleDisappearActions.remove(trackedAction)
        }
        val limit = action.logLimit.evaluate(context.expressionResolver).toInt()
        if (limit == 0) {
            actionHandler.handle(context, action)
            return
        }
        val count = context.sightActionCounts[action] ?: 0
        if (count < limit) {
            context.sightActionCounts[action] = count + 1
            actionHandler.handle(context, action)
        }
    }

    private fun scheduleAction(trackedAction: TrackedAction) {
        cancelAction(trackedAction)
        val (context, action) = trackedAction
        val duration = action.duration.evaluate(context.expressionResolver)
        if (duration == 0L) {
            triggerIfNeeded(trackedAction)
            return
        }
        pendingActions[trackedAction] = mainCoroutineScope.launch {
            delay(duration)
            pendingActions.remove(trackedAction)
            triggerIfNeeded(trackedAction)
        }
    }

    private fun cancelAction(trackedAction: TrackedAction) {
        pendingActions.remove(trackedAction)?.cancel()
    }
}

/**

 * A sight action together with the scope of the element that declares it.

 */
private data class TrackedAction(val context: DivActionHandlingContext, val action: DivSightAction)

@SuppressLint("ComposableNaming")
@Composable
internal fun VisibilityActionTracker.bind() {
    val divViewHost = LocalDivViewHost.current ?: return
    DisposableEffect(this, divViewHost) {
        val contentUnboundCallback = { onContentUnbound() }
        divViewHost.addContentUnboundCallback(contentUnboundCallback)
        onDispose {
            divViewHost.removeContentUnboundCallback(contentUnboundCallback)
        }
    }
}
