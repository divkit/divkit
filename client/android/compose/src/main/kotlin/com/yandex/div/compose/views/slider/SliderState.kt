package com.yandex.div.compose.views.slider

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div2.DivSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

internal class SliderState(
    val main: SliderThumb,
    val secondary: SliderThumb?,
    val range: SliderRange,
) {
    var isInteracting by mutableStateOf(false)
        internal set

    val activeStart: Float
        get() = if (secondary == null) range.minValue
        else min(main.visual.value, secondary.visual.value)

    val activeEnd: Float
        get() = if (secondary == null) main.visual.value
        else max(main.visual.value, secondary.visual.value)

    fun closestThumb(position: Float, trackLength: Float, isRtl: Boolean): SliderThumb {
        val secondary = secondary ?: return main
        val mainPos = main.visual.value.position(range, trackLength, 0f, isRtl)
        val secondaryPos = secondary.visual.value.position(range, trackLength, 0f, isRtl)
        return if (abs(position - mainPos) < abs(position - secondaryPos)) main else secondary
    }
}

internal class SliderThumb(
    val variable: MutableState<Long>,
    val visual: Animatable<Float, AnimationVector1D>,
    private val range: SliderRange,
) {
    fun setValue(newValue: Float, animated: Boolean, scope: CoroutineScope) {
        val coerced = newValue.coerceIn(range.minValue, range.maxValue)
        variable.value = coerced.roundToLong()
        scope.launch {
            if (animated) visual.animateTo(coerced, SLIDER_ANIMATION_SPEC)
            else visual.snapTo(coerced)
        }
    }
}

internal class SliderRange(val minValue: Float, val maxValue: Float) {
    val span: Float get() = maxValue - minValue
}

internal fun Float.position(
    range: SliderRange,
    trackLength: Float,
    trackStart: Float,
    isRtl: Boolean,
): Float {
    val value = coerceIn(range.minValue, range.maxValue)
    val offset = if (isRtl) range.maxValue - value else value - range.minValue
    return trackStart + (trackLength / range.span * offset)
}

@Composable
internal fun DivSlider.rememberSliderState(): SliderState {
    val minValue = minValue.observedValue().toFloat()
    val maxValue = maxValue.observedValue().toFloat().coerceAtLeast(minValue + 1f)
    val range = remember(minValue, maxValue) { SliderRange(minValue, maxValue) }

    val main = rememberThumb(thumbValueVariable, range)
    val secondary = thumbSecondaryValueVariable?.let { rememberThumb(it, range) }

    val state = remember(main, secondary, range) { SliderState(main, secondary, range) }

    SyncVariableToVisual(main, state)
    secondary?.let { SyncVariableToVisual(it, state) }
    return state
}

@Composable
private fun rememberThumb(variableName: String?, range: SliderRange): SliderThumb {
    val minValue = range.minValue.roundToLong()
    val variable = variableName?.let { mutableStateFromVariable(it, minValue) }
        ?: remember { mutableLongStateOf(minValue) }
    val visual = remember {
        Animatable(variable.value.toFloat().coerceIn(range.minValue, range.maxValue))
    }
    return remember(variable, visual, range) { SliderThumb(variable, visual, range) }
}

@Composable
private fun SyncVariableToVisual(thumb: SliderThumb, state: SliderState) {
    LaunchedEffect(thumb, thumb.variable.value, state.isInteracting) {
        if (state.isInteracting) return@LaunchedEffect
        val range = state.range
        val visualValue = thumb.visual.value.coerceIn(range.minValue, range.maxValue)
        if (thumb.variable.value == visualValue.roundToLong())
            return@LaunchedEffect
        val target = thumb.variable.value.toFloat().coerceIn(range.minValue, range.maxValue)
        thumb.visual.animateTo(target, SLIDER_ANIMATION_SPEC)
    }
}

private val SLIDER_ANIMATION_SPEC = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
