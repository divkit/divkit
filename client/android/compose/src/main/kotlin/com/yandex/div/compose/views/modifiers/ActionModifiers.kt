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
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actions(actions: DivActions): Modifier {
    if (actions.tapActions.isEmpty()
        && actions.doubleTapActions.isEmpty()
        && actions.longTapActions.isEmpty()
    ) {
        return this
    }

    val localComponent = LocalComponent.current
    val clickHandler = remember(actions) {
        ClickHandler(actions, localComponent)
    }

    val animation = actions.animation
    val animationName = if (animationsEnabled) {
        animation.name.observedValue()
    } else {
        DivAnimation.Name.NO_ANIMATION
    }
    return when (animationName) {
        DivAnimation.Name.FADE ->
            clickableWithFade(clickHandler = clickHandler, animation = animation)

        DivAnimation.Name.NATIVE ->
            clickable(
                clickHandler = clickHandler,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )

        DivAnimation.Name.NO_ANIMATION ->
            clickable(clickHandler)

        DivAnimation.Name.SCALE,
        DivAnimation.Name.SET,
        DivAnimation.Name.TRANSLATE -> {
            reportError("Animation not supported: $animationName")
            clickable(clickHandler)
        }
    }
}

private fun Modifier.clickable(
    clickHandler: ClickHandler,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource? = null
): Modifier {
    return combinedClickable(
        indication = indication,
        interactionSource = interactionSource,
        onClick = clickHandler.onClick,
        onDoubleClick = clickHandler.onDoubleClick,
        onLongClick = clickHandler.onLongClick
    )
}

@Composable
private fun Modifier.clickableWithFade(
    clickHandler: ClickHandler,
    animation: DivAnimation
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) animation.endValue.observedFloatValue(1f) else 1f,
        animationSpec = tween(durationMillis = animation.duration.observedIntValue()),
        label = "alphaAnimation"
    )
    return graphicsLayer {
        alpha = animatedAlpha
    }.clickable(
        clickHandler = clickHandler,
        interactionSource = interactionSource
    )
}

private class ClickHandler(
    private val actions: DivActions,
    private val localComponent: DivLocalComponent
) {
    val onClick: (() -> Unit) = createHandler(
        actions = actions.tapActions,
        source = DivActionSource.TAP
    ) ?: {}

    val onDoubleClick: (() -> Unit)? = createHandler(
        actions = actions.doubleTapActions,
        source = DivActionSource.DOUBLE_TAP

    )
    val onLongClick: (() -> Unit)? = createHandler(
        actions = actions.longTapActions,
        source = DivActionSource.LONG_TAP
    )

    private fun createHandler(actions: List<DivAction>, source: DivActionSource): (() -> Unit)? {
        if (actions.isEmpty()) {
            return null
        }
        return { localComponent.handleActions(actions, source) }
    }
}
