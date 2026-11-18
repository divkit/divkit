package com.yandex.div.compose.views.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.utils.toEasing
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivSlideTransition

@Composable
internal fun DivAppearanceTransition.frozenAppearance(): DivAppearanceTransition = when (this) {
    is DivAppearanceTransition.Fade -> DivAppearanceTransition.Fade(value.copy(
        alpha = Expression.constant(value.alpha.observedValue()),
        duration = Expression.constant(value.duration.observedValue()),
        startDelay = Expression.constant(value.startDelay.observedValue()),
        interpolator = Expression.constant(value.interpolator.observedValue()),
    ))
    is DivAppearanceTransition.Scale -> DivAppearanceTransition.Scale(value.copy(
        scale = Expression.constant(value.scale.observedValue()),
        pivotX = Expression.constant(value.pivotX.observedValue()),
        pivotY = Expression.constant(value.pivotY.observedValue()),
        duration = Expression.constant(value.duration.observedValue()),
        startDelay = Expression.constant(value.startDelay.observedValue()),
        interpolator = Expression.constant(value.interpolator.observedValue()),
    ))
    is DivAppearanceTransition.Slide -> DivAppearanceTransition.Slide(value.copy(
        edge = Expression.constant(value.edge.observedValue()),
        distance = value.distance?.let { distance ->
            distance.copy(
                value = Expression.constant(distance.value.observedValue()),
                unit = Expression.constant(distance.unit.observedValue()),
            )
        },
        duration = Expression.constant(value.duration.observedValue()),
        startDelay = Expression.constant(value.startDelay.observedValue()),
        interpolator = Expression.constant(value.interpolator.observedValue()),
    ))
    is DivAppearanceTransition.Set -> DivAppearanceTransition.Set(value.copy(
        items = value.items.map { it.frozenAppearance() },
    ))
}

@Composable
internal fun DivAppearanceTransition.animatedAppearance(
    scope: AnimatedVisibilityScope,
    transitionState: EnterExitState,
    baseAlpha: Float,
    exitValues: StateChangeVisualState?,
    slideGeometry: SlideGeometry?,
    label: String,
): AnimatedAppearance {
    return when (this) {
        is DivAppearanceTransition.Fade -> AnimatedAppearance(
            alphas = listOf(
                value.animatedAlpha(
                    scope = scope,
                    transitionState = transitionState,
                    baseAlpha = exitValues?.alpha ?: baseAlpha,
                    label = label,
                )
            ),
        )
        is DivAppearanceTransition.Scale -> AnimatedAppearance(
            scales = listOf(
                value.animatedScale(
                    scope = scope,
                    transitionState = transitionState,
                    exitValues = exitValues,
                    label = label,
                )
            ),
        )
        is DivAppearanceTransition.Slide -> AnimatedAppearance(
            slides = listOf(
                value.animatedSlide(
                    scope = scope,
                    transitionState = transitionState,
                    exitValues = exitValues,
                    slideGeometry = slideGeometry,
                    label = label,
                )
            ),
        )
        is DivAppearanceTransition.Set -> {
            value.items.mapIndexed { index, item ->
                item.animatedAppearance(
                    scope = scope,
                    transitionState = transitionState,
                    baseAlpha = baseAlpha,
                    exitValues = exitValues,
                    slideGeometry = slideGeometry,
                    label = "$label/$index",
                )
            }.sortedByDescending {
                it.orderingDurationMillis
            }.fold(AnimatedAppearance()) { result, item ->
                result.combineWith(item)
            }
        }
    }
}

@Composable
private fun DivFadeTransition.animatedAlpha(
    scope: AnimatedVisibilityScope,
    transitionState: EnterExitState,
    baseAlpha: Float,
    label: String,
): AnimatedFloat {
    val animation = observedAnimation(duration, startDelay, interpolator)
    val transitionAlpha = alpha.observedFloatValue()
    val isAnimated = transitionAlpha != baseAlpha
    val value = scope.transition.animateFloat(
        transitionSpec = { animation.valueSpec(isAnimated) },
        label = "Div state fade $label",
    ) { state ->
        if (state == transitionState) transitionAlpha else baseAlpha
    }.value
    return AnimatedFloat(
        value = value,
        timing = scope.animatedTiming(transitionState, animation, isAnimated, label),
        orderingDurationMillis = animation.endTimeMillis,
    )
}

@Composable
private fun DivScaleTransition.animatedScale(
    scope: AnimatedVisibilityScope,
    transitionState: EnterExitState,
    exitValues: StateChangeVisualState?,
    label: String,
): AnimatedScale {
    val animation = observedAnimation(duration, startDelay, interpolator)
    val transitionScale = scale.observedFloatValue()
    val transitionPivotX = pivotX.observedFloatValue()
    val transitionPivotY = pivotY.observedFloatValue()
    val baseScale = exitValues?.scale ?: 1f
    val isAnimated = transitionScale != baseScale
    val animatedScale = scope.transition.animateFloat(
        transitionSpec = { animation.valueSpec(isAnimated) },
        label = "Div state scale $label",
    ) { state ->
        if (state == transitionState) transitionScale else baseScale
    }.value
    return AnimatedScale(
        value = animatedScale,
        pivotX = transitionPivotX,
        pivotY = transitionPivotY,
        timing = scope.animatedTiming(transitionState, animation, isAnimated, label),
        orderingDurationMillis = animation.endTimeMillis,
    )
}

@Composable
private fun DivSlideTransition.animatedSlide(
    scope: AnimatedVisibilityScope,
    transitionState: EnterExitState,
    exitValues: StateChangeVisualState?,
    slideGeometry: SlideGeometry?,
    label: String,
): AnimatedSlide {
    val animation = observedAnimation(duration, startDelay, interpolator)
    val transitionEdge = edge.observedValue()
    val transitionDistance = distance?.observedPxValue()
    val slide = AnimatedSlide(
        progress = 0f,
        edge = transitionEdge,
        distance = transitionDistance,
        exitTranslationX = exitValues?.translationX ?: 0f,
        exitTranslationY = exitValues?.translationY ?: 0f,
        orderingDurationMillis = animation.endTimeMillis,
    )
    val isExiting = transitionState == EnterExitState.PostExit
    val isAnimated = slide.hasMovement(slideGeometry, isExiting)
    val progress = scope.transition.animateFloat(
        transitionSpec = { animation.valueSpec(isAnimated) },
        label = "Div state slide $label",
    ) { state ->
        if (state == transitionState) 1f else 0f
    }.value
    return slide.copy(
        progress = progress,
        timing = scope.animatedTiming(transitionState, animation, isAnimated, label),
    )
}

@Composable
private fun observedAnimation(
    duration: Expression<Long>,
    startDelay: Expression<Long>,
    interpolator: Expression<DivAnimationInterpolator>,
): ObservedAnimation {
    val durationMillis = duration.observedIntValue()
    val delayMillis = startDelay.observedIntValue()
    return ObservedAnimation(
        valueSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = interpolator.observedValue().toEasing(),
        ),
        timingSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = LinearEasing,
        ),
        immediateSpec = tween(durationMillis = 0),
        startTimeMillis = delayMillis.toLong(),
        endTimeMillis = durationMillis.toLong() + delayMillis,
    )
}

@Composable
private fun AnimatedVisibilityScope.animatedTiming(
    transitionState: EnterExitState,
    animation: ObservedAnimation,
    isAnimated: Boolean,
    label: String,
): AnimatedTiming {
    val transitionPhase = if (transitionState == EnterExitState.PreEnter) 0f else 1f
    val phase = transition.animateFloat(
        transitionSpec = { if (isAnimated) animation.timingSpec else animation.immediateSpec },
        label = "Div state timing $label",
    ) { state ->
        if (state == transitionState) transitionPhase else 1f - transitionPhase
    }.value
    return AnimatedTiming(
        phase = phase,
        startTimeMillis = animation.startTimeMillis,
        endTimeMillis = if (isAnimated) animation.endTimeMillis else 0L,
        hasAnimator = isAnimated,
    )
}

private data class ObservedAnimation(
    val valueSpec: TweenSpec<Float>,
    val timingSpec: TweenSpec<Float>,
    val immediateSpec: TweenSpec<Float>,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
) {
    fun valueSpec(isAnimated: Boolean): TweenSpec<Float> = if (isAnimated) valueSpec else immediateSpec
}
