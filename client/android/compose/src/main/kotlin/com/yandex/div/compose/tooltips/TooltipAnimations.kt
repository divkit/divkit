package com.yandex.div.compose.tooltips

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltip.Position

internal const val DEFAULT_TOOLTIP_TRANSITION_DURATION_MS = 300

@Composable
internal fun DivTooltip.observedEnterTransition(): EnterTransition {
    return animationIn?.toEnterTransition(position.observedValue()) ?: defaultEnterTransition
}

@Composable
internal fun DivTooltip.observedExitTransition(): ExitTransition {
    return animationOut?.toExitTransition(position.observedValue()) ?: defaultExitTransition
}

@Composable
private fun DivAnimation.toEnterTransition(position: Position): EnterTransition {
    return when (name.observedValue()) {
        DivAnimation.Name.FADE ->
            fadeIn(
                animationSpec = observedAnimationSpec(),
                initialAlpha = startValue.observedFloatValue(0f)
            )

        DivAnimation.Name.TRANSLATE -> {
            val percentage = startValue.observedFloatValue(0f)
            slideIn(animationSpec = observedAnimationSpec()) { size ->
                position.translateOffset(size, percentage)
            }
        }

        DivAnimation.Name.SCALE ->
            scaleIn(
                animationSpec = observedAnimationSpec(),
                initialScale = startValue.observedFloatValue(0f)
            )

        DivAnimation.Name.SET -> {
            var transition = EnterTransition.None
            items?.forEach {
                transition += it.toEnterTransition(position)
            }
            transition
        }

        DivAnimation.Name.NATIVE, DivAnimation.Name.NO_ANIMATION -> EnterTransition.None
    }
}

@Composable
private fun DivAnimation.toExitTransition(position: Position): ExitTransition {
    return when (name.observedValue()) {
        DivAnimation.Name.FADE ->
            fadeOut(
                animationSpec = observedAnimationSpec(),
                targetAlpha = endValue.observedFloatValue(0f)
            )

        DivAnimation.Name.TRANSLATE -> {
            val percentage = endValue.observedFloatValue(0f)
            slideOut(animationSpec = observedAnimationSpec()) { size ->
                position.translateOffset(size, percentage)
            }
        }

        DivAnimation.Name.SCALE ->
            scaleOut(
                animationSpec = observedAnimationSpec(),
                targetScale = endValue.observedFloatValue(0f)
            )

        DivAnimation.Name.SET -> {
            var transition = ExitTransition.None
            items?.forEach {
                transition += it.toExitTransition(position)
            }
            transition
        }

        DivAnimation.Name.NATIVE, DivAnimation.Name.NO_ANIMATION -> ExitTransition.None
    }
}

@Composable
private fun <T> DivAnimation.observedAnimationSpec(): TweenSpec<T> {
    val easing = when (interpolator.observedValue()) {
        DivAnimationInterpolator.LINEAR -> LinearEasing
        DivAnimationInterpolator.EASE -> FastOutSlowInEasing
        DivAnimationInterpolator.EASE_IN -> FastOutLinearInEasing
        DivAnimationInterpolator.EASE_OUT -> LinearOutSlowInEasing
        DivAnimationInterpolator.EASE_IN_OUT -> FastOutSlowInEasing
        DivAnimationInterpolator.SPRING -> springEasing
    }
    return tween(
        durationMillis = duration.observedIntValue(),
        delayMillis = startDelay.observedIntValue(),
        easing = easing
    )
}

private val defaultEnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = DEFAULT_TOOLTIP_TRANSITION_DURATION_MS,
        easing = FastOutSlowInEasing
    )
)

private val defaultExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = DEFAULT_TOOLTIP_TRANSITION_DURATION_MS,
        easing = FastOutSlowInEasing
    )
)

private val springEasing = CubicBezierEasing(0.5f, 1.7f, 0.5f, 1f)

private data class SlideDirection(val x: Float, val y: Float)

private val Position.slideDirection: SlideDirection
    get() = SlideDirection(
        x = when (this) {
            Position.TOP_LEFT, Position.LEFT, Position.BOTTOM_LEFT -> 1f
            Position.TOP_RIGHT, Position.RIGHT, Position.BOTTOM_RIGHT -> -1f
            Position.CENTER -> 0.5f
            Position.TOP, Position.BOTTOM -> 0f
        },
        y = when (this) {
            Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT -> 1f
            Position.BOTTOM_LEFT, Position.BOTTOM, Position.BOTTOM_RIGHT -> -1f
            Position.CENTER -> 0.5f
            Position.LEFT, Position.RIGHT -> 0f
        }
    )

private fun Position.translateOffset(
    size: IntSize,
    percentage: Float,
): IntOffset {
    val direction = slideDirection
    return IntOffset(
        x = (direction.x * size.width * percentage).toInt(),
        y = (direction.y * size.height * percentage).toInt()
    )
}
