package com.yandex.div.compose.views.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actions(data: Div): Modifier {
    val actionParams = data.observedActionParams() ?: return this

    if (actionParams.tapActions.isEmpty()
        && actionParams.doubleTapActions.isEmpty()
        && actionParams.longTapActions.isEmpty()
    ) {
        return this
    }

    return when (val name = actionParams.animation.name.observedValue()) {
        DivAnimation.Name.FADE ->
            clickableWithFade(actionParams)

        DivAnimation.Name.NATIVE ->
            clickable(
                actionParams = actionParams,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )

        DivAnimation.Name.NO_ANIMATION ->
            clickable(actionParams)

        DivAnimation.Name.SCALE,
        DivAnimation.Name.SET,
        DivAnimation.Name.TRANSLATE -> {
            reportError("Animation not supported: $name")
            clickable(actionParams)
        }
    }
}

private fun DivLocalComponent.createHandler(
    actions: List<DivAction>,
    source: DivActionSource
): (() -> Unit)? {
    if (actions.isEmpty()) {
        return null
    }
    return { actionHandler.handle(actionHandlingContext, actions, source) }
}

@Composable
private fun Modifier.clickable(
    actionParams: ActionParams,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource? = null
): Modifier {
    val localComponent = LocalComponent.current
    return combinedClickable(
        indication = indication,
        interactionSource = interactionSource,
        onClick = localComponent.createHandler(
            actions = actionParams.tapActions,
            source = DivActionSource.TAP
        ) ?: {},
        onDoubleClick = localComponent.createHandler(
            actions = actionParams.doubleTapActions,
            source = DivActionSource.DOUBLE_TAP
        ),
        onLongClick = localComponent.createHandler(
            actions = actionParams.longTapActions,
            source = DivActionSource.LONG_TAP
        )
    )
}

@Composable
private fun Modifier.clickableWithFade(actionParams: ActionParams): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animation = actionParams.animation
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) animation.endValue.observedFloatValue(1f) else 1f,
        animationSpec = tween(durationMillis = animation.duration.observedIntValue()),
        label = "alphaAnimation"
    )
    return graphicsLayer(alpha = animatedAlpha)
        .clickable(
            interactionSource = interactionSource,
            actionParams = actionParams
        )
}

private class ActionParams(
    val tapActions: List<DivAction>,
    val doubleTapActions: List<DivAction>,
    val longTapActions: List<DivAction>,
    val animation: DivAnimation
)

@Composable
private fun getParams(
    actions: List<DivAction>?,
    action: DivAction?,
    animation: DivAnimation,
    doubleTapActions: List<DivAction>?,
    longTapActions: List<DivAction>?
) = ActionParams(
    tapActions = (actions ?: action?.let { listOf(it) }).observedEnabledActions(),
    doubleTapActions = doubleTapActions.observedEnabledActions(),
    longTapActions = longTapActions.observedEnabledActions(),
    animation = animation
)

@Composable
private fun List<DivAction>?.observedEnabledActions(): List<DivAction> {
    return this?.filter { it.isEnabled.observedValue() } ?: emptyList()
}

@Composable
private fun Div.observedActionParams(): ActionParams? {
    return when (this) {
        is Div.Container ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Image ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.GifImage ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Grid ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Separator ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.State ->
            getParams(
                actions = value.actions,
                action = value.action,
                animation = value.actionAnimation,
                doubleTapActions = value.doubletapActions,
                longTapActions = value.longtapActions
            )

        is Div.Text ->
            getParams(
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
