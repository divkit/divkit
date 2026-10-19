package com.yandex.div.compose.actions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    private val counter = mutableMapOf<DivSightAction, Int>()
    private val pendingActions = mutableMapOf<DivSightAction, Job>()
    private val eligibleDisappearActions = mutableMapOf<DivDisappearAction, DivActionHandlingContext>()

    fun isLimitReached(action: DivSightAction, limit: Int): Boolean {
        return limit > 0 && ((counter[action] ?: 0) >= limit)
    }

    fun onVisibilityChanged(
        context: DivActionHandlingContext,
        action: DivVisibilityAction,
        isVisible: Boolean
    ) {
        if (!isVisible) {
            cancelAction(action)
        } else {
            scheduleAction(context, action)
        }
    }

    fun onVisibilityChanged(
        context: DivActionHandlingContext,
        action: DivDisappearAction,
        isVisible: Boolean
    ) {
        if (isVisible) {
            eligibleDisappearActions[action] = context
            cancelAction(action)
        } else {
            scheduleAction(context, action)
        }
    }

    fun onContentUnbound() {
        val actions = eligibleDisappearActions.toMap()
        eligibleDisappearActions.clear()
        actions.keys.forEach(::cancelAction)
        actions.forEach { (action, context) ->
            triggerIfNeeded(context, action)
        }
    }

    private fun triggerIfNeeded(
        context: DivActionHandlingContext,
        action: DivSightAction
    ) {
        if (action is DivDisappearAction) {
            eligibleDisappearActions.remove(action)
        }
        val limit = action.logLimit.evaluate(context.expressionResolver).toInt()
        val count = counter[action] ?: 0
        val shouldTrigger = limit == 0 || count < limit
        if (shouldTrigger) {
            counter[action] = count + 1
            actionHandler.handle(context, action)
        }
    }

    private fun scheduleAction(
        context: DivActionHandlingContext,
        action: DivSightAction
    ) {
        cancelAction(action)
        val duration = action.duration.evaluate(context.expressionResolver)
        if (duration == 0L) {
            triggerIfNeeded(context, action)
            return
        }
        pendingActions[action] = mainCoroutineScope.launch {
            delay(duration)
            pendingActions.remove(action)
            triggerIfNeeded(context, action)
        }
    }

    private fun cancelAction(action: DivSightAction) {
        pendingActions.remove(action)?.cancel()
    }
}

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
