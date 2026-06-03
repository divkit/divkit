package com.yandex.div.compose.actions

import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

internal class DivActions(
    val tapActions: List<DivAction>,
    val doubleTapActions: List<DivAction>,
    val longTapActions: List<DivAction>,
    val animation: DivAnimation
)

@Composable
internal fun Div.observedActions(): DivActions? {
    return when (this) {
        is Div.Container ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Image ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.GifImage ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Grid ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Separator ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.State ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Text ->
            observedActions(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Custom,
        is Div.Gallery,
        is Div.Indicator,
        is Div.Input,
        is Div.Pager,
        is Div.Select,
        is Div.Slider,
        is Div.Switch,
        is Div.Tabs,
        is Div.Video -> null
    }
}

@Composable
private fun observedActions(
    actions: List<DivAction>?,
    action: DivAction?,
    animation: DivAnimation,
    doubleTapActions: List<DivAction>?,
    longTapActions: List<DivAction>?
) = DivActions(
    tapActions = (actions ?: action?.let { listOf(it) }).observedEnabledActions(),
    doubleTapActions = doubleTapActions.observedEnabledActions(),
    longTapActions = longTapActions.observedEnabledActions(),
    animation = animation
)

@Composable
private fun List<DivAction>?.observedEnabledActions(): List<DivAction> {
    return this?.filter { it.isEnabled.observedValue() } ?: emptyList()
}
