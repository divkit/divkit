package com.yandex.div.compose.actions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedAnimationSpec
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actionAnimation(
    animation: DivAnimation,
    interactionSource: MutableInteractionSource,
): Modifier {
    val component = LocalComponent.current
    val animationName = if (component.animationConfiguration.isEnabled) {
        animation.name.observedValue()
    } else {
        DivAnimation.Name.NO_ANIMATION
    }
    return when (animationName) {
        DivAnimation.Name.FADE ->
            this.fadeOnPress(animation = animation, interactionSource = interactionSource)

        DivAnimation.Name.NATIVE ->
            this.indication(interactionSource = interactionSource, indication = ripple())

        DivAnimation.Name.SCALE ->
            this.scaleOnPress(animation = animation, interactionSource = interactionSource)

        DivAnimation.Name.SET,
        DivAnimation.Name.TRANSLATE -> {
            reportError("Animation not supported: $animationName")
            this
        }

        DivAnimation.Name.NO_ANIMATION -> this
    }
}

@Composable
private fun Modifier.fadeOnPress(
    animation: DivAnimation,
    interactionSource: MutableInteractionSource
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) {
            animation.endValue.observedFloatValue(0.6f).coerceIn(0f, 1f)
        } else {
            1f
        },
        animationSpec = animation.observedAnimationSpec(),
        label = "alphaAnimation"
    )
    return graphicsLayer {
        alpha = animatedAlpha
    }
}

@Composable
private fun Modifier.scaleOnPress(
    animation: DivAnimation,
    interactionSource: MutableInteractionSource
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) {
            animation.endValue.observedFloatValue(0.95f).coerceAtLeast(0f)
        } else {
            1f
        },
        animationSpec = animation.observedAnimationSpec(),
        label = "scaleAnimation"
    )
    return graphicsLayer {
        scaleX = animatedScale
        scaleY = animatedScale
    }
}
