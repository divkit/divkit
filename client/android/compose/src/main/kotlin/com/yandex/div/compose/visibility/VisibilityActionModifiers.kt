package com.yandex.div.compose.visibility

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
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
    if (actions.isEmpty()) {
        return this
    }

    val component = LocalComponent.current
    val actionHandlingContext = component.actionHandlingContext
    val visibilityActionTracker = component.visibilityActionTracker
    var modifier = this
    actions
        .filter {
            visibilityActionTracker.shouldRegisterVisibilityCallback(
                context = actionHandlingContext,
                action = it,
            )
        }
        .forEach { action ->
            modifier = modifier.onDivVisibilityChanged(
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
    if (actions.isEmpty()) {
        return this
    }

    val component = LocalComponent.current
    val actionHandlingContext = component.actionHandlingContext
    val visibilityActionTracker = component.visibilityActionTracker
    var modifier = this
    actions
        .filter {
            visibilityActionTracker.shouldRegisterVisibilityCallback(
                context = actionHandlingContext,
                action = it,
            )
        }
        .forEach { action ->
            modifier = modifier.onDivVisibilityChanged(
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
private fun VisibilityActionTracker.shouldRegisterVisibilityCallback(
    context: DivActionHandlingContext,
    action: DivSightAction
): Boolean {
    return !isLimitReached(
        context = context,
        action = action,
        limit = action.logLimit.observedIntValue()
    )
}
