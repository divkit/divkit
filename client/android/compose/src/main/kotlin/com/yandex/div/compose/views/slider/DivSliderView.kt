package com.yandex.div.compose.views.slider

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.Layout
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.isRtlLayout
import com.yandex.div2.DivSlider
import kotlin.math.roundToInt

@Composable
internal fun DivSliderView(modifier: Modifier, data: DivSlider) {
    val state = data.rememberSliderState()

    val styles = data.observeSliderStyles(
        range = state.range,
        hasSecondary = state.secondary != null
    ) ?: return

    val isEnabled = data.isEnabled.observedValue()
    val isRtlLayout = isRtlLayout
    val animationsEnabled = animationsEnabled
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = if (data.focus != null) remember { FocusRequester() } else null
    val localComponent = LocalComponent.current
    val pressHandler = remember(data.pressStartActions, data.pressEndActions, localComponent, focusRequester) {
        SliderPressHandler(data.pressStartActions, data.pressEndActions, localComponent, focusRequester)
    }
    val thumbWidth = styles.maxTickOrThumbWidth
    val hasTickMarks = styles.hasTickMarks
    val gestureHandler = remember(
        state, thumbWidth, hasTickMarks, isRtlLayout, animationsEnabled, coroutineScope, pressHandler
    ) {
        SliderGestureHandler(
            state = state,
            thumbWidth = thumbWidth,
            hasTickMarks = hasTickMarks,
            isRtl = isRtlLayout,
            animationsEnabled = animationsEnabled,
            coroutineScope = coroutineScope,
            pressHandler = pressHandler,
        )
    }

    val modifier = modifier
        .applyIfNotNull(focusRequester) { focusRequester(it).focusable(enabled = isEnabled) }
        .sliderPointerInput(gestureHandler, isEnabled)
        .drawBehind { drawSlider(state, styles, isRtl = isRtlLayout) }

    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            val width = if (constraints.hasFixedWidth) {
                constraints.maxWidth
            } else {
                styles.desiredWidth.roundToInt()
                    .coerceIn(constraints.minWidth, constraints.maxWidth)
            }
            val height = if (constraints.hasFixedHeight) {
                constraints.maxHeight
            } else {
                styles.desiredHeight.roundToInt()
                    .coerceIn(constraints.minHeight, constraints.maxHeight)
            }

            layout(width, height) {}
        }
    )
}
