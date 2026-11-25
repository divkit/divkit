package com.yandex.div.compose.views.slider

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.variables.mutableStateFromVariable
import com.yandex.div2.DivSlider
import kotlin.math.roundToLong

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
    val isAnimated = LocalComponent.current.animationConfiguration.isEnabled
    LaunchedEffect(thumb, thumb.variable.value, state.isInteracting) {
        val value = thumb.applyVariableValue()
        if (state.isInteracting || value == thumb.visual.value.roundToLong()) return@LaunchedEffect
        val target = value.toFloat()
        if (isAnimated) {
            thumb.visual.animateTo(target, SLIDER_ANIMATION_SPEC)
        } else {
            thumb.visual.snapTo(target)
        }
    }
}
