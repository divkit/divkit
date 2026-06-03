package com.yandex.div.compose.views.slider

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivSlider
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
internal fun DivSliderView(modifier: Modifier, data: DivSlider) {
    val state = data.rememberSliderState()
    val styles = data.observeSliderStyles(state.range, hasSecondary = state.secondary != null)
    val isEnabled = data.isEnabled.observedValue()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val coroutineScope = rememberCoroutineScope()

    val modifier = modifier
        .sliderPointerInput(state, styles, isEnabled, isRtl, coroutineScope)
        .drawBehind { drawSlider(state, styles, isRtl = isRtl) }

    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            val width = if (constraints.hasFixedWidth) {
                constraints.maxWidth
            } else {
                styles.desiredWidth.roundToInt().coerceIn(constraints.minWidth, constraints.maxWidth)
            }
            val height = if (constraints.hasFixedHeight) {
                constraints.maxHeight
            } else {
                styles.desiredHeight.roundToInt().coerceIn(constraints.minHeight, constraints.maxHeight)
            }

            layout(width, height) {}
        }
    )
}

private fun Modifier.sliderPointerInput(
    state: SliderState,
    styles: SliderStyles,
    isEnabled: Boolean,
    isRtl: Boolean,
    coroutineScope: CoroutineScope,
): Modifier {
    if (!isEnabled)
        return this
    val thumbWidth = styles.maxTickOrThumbWidth
    val hasTickMarks = styles.hasTickMarks
    return pointerInput(state, styles, isRtl) {
        awaitEachGesture {
            val down = awaitFirstDown()
            val trackLength = size.width - thumbWidth
            if (trackLength <= 0f) return@awaitEachGesture

            val activeThumb = state.closestThumb(
                position = down.position.x - thumbWidth / 2f,
                trackLength = trackLength,
                isRtl = isRtl
            )

            fun valueAt(x: Float): Float {
                val position = (x - thumbWidth / 2f).coerceIn(0f, trackLength)
                val offset = position * state.range.span / trackLength
                val value = if (isRtl) {
                    state.range.maxValue - offset
                } else {
                    state.range.minValue + offset
                }
                return if (hasTickMarks) value.roundToLong().toFloat() else value
            }

            state.isInteracting = true
            try {
                activeThumb.setValue(valueAt(down.position.x), animated = true, scope = coroutineScope)
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break
                    if (!change.pressed) break
                    activeThumb.setValue(valueAt(change.position.x), animated = false, scope = coroutineScope)
                    change.consume()
                }
            } finally {
                state.isInteracting = false
            }
        }
    }
}
