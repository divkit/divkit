package com.yandex.div.compose.actions

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toEasing
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actionAnimation(
    animation: DivAnimation,
    interactionSource: MutableInteractionSource,
): Modifier {
    val animationName = if (animationsEnabled) {
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
        animationSpec = animation.toAnimationSpec(),
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
        animationSpec = animation.toAnimationSpec(),
        label = "scaleAnimation"
    )
    return graphicsLayer {
        scaleX = animatedScale
        scaleY = animatedScale
    }
}

@Composable
private fun DivAnimation.toAnimationSpec(): AnimationSpec<Float> {
    return tween(
        durationMillis = duration.observedIntValue(),
        delayMillis = startDelay.observedIntValue(),
        easing = interpolator.observedValue().toEasing(),
    )
}

