package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import com.yandex.div.compose.context.LocalDivContext
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivBase
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction

@Composable
internal fun Modifier.visibilityActions(data: DivBase): Modifier {
    val visibilityActions = data.visibilityActions
        ?: data.visibilityAction?.let { listOf(it) }
        ?: emptyList()
    return this
        .visibilityActions(visibilityActions)
        .disappearActions(data.disappearActions.orEmpty())
}

@Composable
private fun Modifier.visibilityActions(actions: List<DivVisibilityAction>): Modifier {
    val visibilityActionTracker = divContext.component.visibilityActionTracker
    val actionHandlingContext = LocalDivContext.current.actionHandlingContext
    var modifier = this
    actions
        .filter { shouldRegisterVisibilityCallback(it) }
        .forEach { action ->
            modifier = modifier.onVisibilityChanged(
                minDurationMs = action.visibilityDuration.observedValue(),
                minFractionVisible = action.visibilityPercentage.observedValue() / 100f
            ) {
                visibilityActionTracker.onVisibilityChanged(
                    context = actionHandlingContext,
                    action = action,
                    isVisible = it
                )
            }
        }
    return modifier
}

@Composable
private fun Modifier.disappearActions(actions: List<DivDisappearAction>): Modifier {
    val visibilityActionTracker = divContext.component.visibilityActionTracker
    val actionHandlingContext = LocalDivContext.current.actionHandlingContext
    var modifier = this
    actions
        .filter { shouldRegisterVisibilityCallback(it) }
        .forEach { action ->
            modifier = modifier.onVisibilityChanged(
                minFractionVisible = action.visibilityPercentage.observedValue() / 100f
            ) {
                visibilityActionTracker.onVisibilityChanged(
                    context = actionHandlingContext,
                    action = action,
                    isVisible = it
                )
            }
        }
    return modifier
}

@Composable
private fun shouldRegisterVisibilityCallback(action: DivSightAction): Boolean {
    return !divContext.component.visibilityActionTracker.isLimitReached(
        action = action,
        limit = action.logLimit.observedValue().toInt()
    )
}
