package com.yandex.div.compose.views.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actions(data: Div): Modifier {
    val actionParams = data.actionParams ?: return this

    val actions = actionParams.actions.filter { it.isEnabled.observedValue() }
    if (actions.isEmpty()) {
        return this
    }

    val actionHandler = divContext.component.actionHandler
    val actionHandlingContext = LocalComponent.current.actionHandlingContext
    val onClick: () -> Unit = {
        actionHandler.handle(actionHandlingContext, actions, source = DivActionSource.TAP)
    }

    return when (val name = actionParams.animation.name.observedValue()) {
        DivAnimation.Name.FADE ->
            clickableWithFade(animation = actionParams.animation, onClick = onClick)

        DivAnimation.Name.NATIVE ->
            clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )

        DivAnimation.Name.NO_ANIMATION ->
            clickable(indication = null, interactionSource = null, onClick = onClick)

        DivAnimation.Name.SCALE,
        DivAnimation.Name.SET,
        DivAnimation.Name.TRANSLATE -> {
            reportError("Animation not supported: $name")
            clickable(indication = null, interactionSource = null, onClick = onClick)
        }
    }
}

private class ActionParams(
    val actions: List<DivAction>,
    val animation: DivAnimation
)

private fun getParams(
    actions: List<DivAction>?,
    action: DivAction?,
    animation: DivAnimation
) = ActionParams(
    actions = actions ?: action?.let { listOf(it) } ?: emptyList(),
    animation = animation
)

private val Div.actionParams: ActionParams?
    get() {
        return when (this) {
            is Div.Container -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.Image -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.GifImage -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.Grid -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.Separator -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.State -> getParams(value.actions, value.action, value.actionAnimation)
            is Div.Text -> getParams(value.actions, value.action, value.actionAnimation)
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
private fun Modifier.clickableWithFade(
    animation: DivAnimation,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) animation.endValue.observedFloatValue(1f) else 1f,
        animationSpec = tween(durationMillis = animation.duration.observedIntValue()),
        label = "alphaAnimation"
    )
    return graphicsLayer(alpha = animatedAlpha)
        .clickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = onClick
        )
}
