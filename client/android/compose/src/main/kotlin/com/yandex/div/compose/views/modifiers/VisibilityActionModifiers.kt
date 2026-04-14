package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.utils.observedIntValue
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
    val visibilityActionTracker = LocalDivViewContext.current.visibilityActionTracker
    val actionHandlingContext = LocalComponent.current.actionHandlingContext
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
    val visibilityActionTracker = LocalDivViewContext.current.visibilityActionTracker
    val actionHandlingContext = LocalComponent.current.actionHandlingContext
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
    return !LocalDivViewContext.current.visibilityActionTracker.isLimitReached(
        action = action,
        limit = action.logLimit.observedIntValue()
    )
}
