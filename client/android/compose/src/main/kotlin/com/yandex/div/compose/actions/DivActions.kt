package com.yandex.div.compose.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

@Stable
internal data class DivActions(
    val tapActions: List<DivAction>,
    val doubleTapActions: List<DivAction>?,
    val longTapActions: List<DivAction>?,
    val animation: DivAnimation,
    val captureFocusOnAction: Expression<Boolean>,
)

internal fun Div.observedActions(): DivActions? = when (this) {
    is Div.Container -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.Image -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.GifImage -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.Grid -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.Separator -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.State -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

    is Div.Text -> with(value) {
        observedActions(actions, action, actionAnimation, captureFocusOnAction, doubletapActions, longtapActions)
    }

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

private fun observedActions(
    actions: List<DivAction>?,
    action: DivAction?,
    animation: DivAnimation,
    captureFocusOnAction: Expression<Boolean>,
    doubleTapActions: List<DivAction>?,
    longTapActions: List<DivAction>?
): DivActions? {
    val tapActions = actions ?: action?.let { listOf(it) }
    if (tapActions.isNullOrEmpty() && doubleTapActions.isNullOrEmpty() && longTapActions.isNullOrEmpty()) {
        return null
    }
    return DivActions(
        tapActions = tapActions.orEmpty(),
        doubleTapActions = doubleTapActions?.takeIf { it.isNotEmpty() },
        longTapActions = longTapActions?.takeIf { it.isNotEmpty() },
        animation = animation,
        captureFocusOnAction = captureFocusOnAction,
    )
}

@Composable
internal fun List<DivAction>?.observedEnabledActions(): List<DivAction> {
    return this?.filter { it.isEnabled.observedValue() } ?: emptyList()
}
