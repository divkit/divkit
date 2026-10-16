package com.yandex.div.internal.tooltip

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.Div
import com.yandex.div2.DivAction

@InternalApi
fun Div.hasTooltipActions(): Boolean {
    val divBase = value()
    if (!divBase.selectedActions.isNullOrEmpty()) {
        return true
    }

    return when (this) {
        is Div.Container -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.items?.any { it.hasTooltipActions() } == true

        is Div.Custom -> value.items?.any { it.hasTooltipActions() } == true
        is Div.Gallery -> true
        is Div.GifImage -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Grid -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.items?.any { it.hasTooltipActions() } == true

        is Div.Image -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Indicator -> false
        is Div.Input -> true
        is Div.Pager -> true
        is Div.Select -> true
        is Div.Separator -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Slider -> true
        is Div.State -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.states.any { state -> state.div?.hasTooltipActions() == true }

        is Div.Switch -> true
        is Div.Tabs -> true
        is Div.Text -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Video -> false
    }
}

private fun hasActions(
    action: DivAction?,
    actions: List<DivAction>?,
    doubletapActions: List<DivAction>?,
    longtapActions: List<DivAction>?,
): Boolean {
    return action != null ||
        !actions.isNullOrEmpty() ||
        !doubletapActions.isNullOrEmpty() ||
        !longtapActions.isNullOrEmpty()
}
