package com.yandex.div.compose.actions

import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.internal.util.duration
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@DivViewScope
internal class VisibilityActionTracker @Inject constructor(
    private val actionHandler: DivActionHandler,
    private val coroutineScope: CoroutineScope
) {
    private val counter = mutableMapOf<DivSightAction, Int>()
    private val pendingActions = mutableMapOf<DivSightAction, Job>()

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
            cancelAction(action)
        } else {
            scheduleAction(context, action)
        }
    }

    private fun triggerIfNeeded(
        context: DivActionHandlingContext,
        action: DivSightAction
    ) {
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
        pendingActions[action] = coroutineScope.launch {
            delay(action.duration.evaluate(context.expressionResolver))
            pendingActions.remove(action)
            triggerIfNeeded(context, action)
        }
    }

    private fun cancelAction(action: DivSightAction) {
        pendingActions.remove(action)?.cancel()
    }
}
