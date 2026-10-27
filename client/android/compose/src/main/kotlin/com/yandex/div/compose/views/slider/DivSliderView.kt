package com.yandex.div.compose.views.slider

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Constraints
import com.yandex.div.compose.R
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.isRtlLayout
import com.yandex.div.compose.views.modifiers.observeContentDescription
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivSlider
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sign

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

    val isExcluded = data.accessibility?.mode?.observedValue() == DivAccessibility.Mode.EXCLUDE
    val modifier = modifier
        .applyIf(isExcluded) { clearAndSetSemantics {} }
        .applyIfNotNull(focusRequester) { focusRequester(it).focusable(enabled = isEnabled) }
        .sliderPointerInput(gestureHandler, isEnabled)
        .drawBehind { drawSlider(state, styles, isRtl = isRtlLayout) }

    Layout(
        content = {
            if (!isExcluded) {
                SliderThumbNodes(data, state, isEnabled, coroutineScope)
            }
        },
        modifier = modifier,
        measurePolicy = sliderMeasurePolicy(state, styles, isRtlLayout),
    )
}

private fun sliderMeasurePolicy(
    state: SliderState,
    styles: SliderStyles,
    isRtl: Boolean,
): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
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
        val thumbs = listOfNotNull(
            state.main to styles.thumb,
            state.secondary?.let { thumb -> styles.secondaryThumb?.let { thumb to it } },
        ).take(measurables.size)
        val trackLength = width - styles.maxTickOrThumbWidth
        val trackStart = styles.maxTickOrThumbWidth / 2f
        // The nodes follow the thumb values, not the animated thumb positions: a read of the animation here
        // would remeasure the slider on every frame of a thumb animation.
        val centers = thumbs.map { (thumb, _) ->
            thumb.variable.value.toFloat().position(state.range, trackLength, trackStart, isRtl)
        }
        val nodes = thumbs.mapIndexed { index, (_, drawable) ->
            val (left, right) = thumbNodeSpan(centers, index, drawable.width, isRtl)
            val nodeConstraints = Constraints.fixed(right - left, drawable.height.roundToInt())
            left to measurables[index].measure(nodeConstraints)
        }
        layout(width, height) {
            nodes.forEach { (left, placeable) ->
                placeable.place(x = left, y = (height - placeable.height) / 2)
            }
        }
    }
}

private fun thumbNodeSpan(
    centers: List<Float>,
    index: Int,
    width: Float,
    isRtl: Boolean,
): Pair<Int, Int> {
    val center = centers[index]
    val left = center - width / 2f
    val right = center + width / 2f
    val other = centers.getOrNull(1 - index) ?: return left.roundToInt() to right.roundToInt()
    val middle = (center + other) / 2f
    return if (center < other || center == other && (index == 0) != isRtl) {
        left.roundToInt() to min(right, middle).roundToInt()
    } else {
        max(left, middle).roundToInt() to right.roundToInt()
    }
}

@Composable
private fun SliderThumbNodes(
    data: DivSlider,
    state: SliderState,
    isEnabled: Boolean,
    scope: CoroutineScope,
) {
    val description = data.accessibility?.observeContentDescription()
    val secondary = state.secondary
    if (secondary == null) {
        SliderThumbNode(state.main, state.range, description, isEnabled, scope)
        return
    }
    val start = joinDescription(
        description,
        stringResource(R.string.div_slider_range_start),
    )
    SliderThumbNode(state.main, state.range, start, isEnabled, scope)
    val secondaryAccessibility = data.secondaryValueAccessibility
    if (secondaryAccessibility?.mode?.observedValue() == DivAccessibility.Mode.EXCLUDE) return
    val end = secondaryAccessibility?.observeContentDescription()
        ?: joinDescription(description, stringResource(R.string.div_slider_range_end))
    SliderThumbNode(secondary, state.range, end, isEnabled, scope)
}

@Composable
private fun SliderThumbNode(
    thumb: SliderThumb,
    range: SliderRange,
    description: String?,
    isEnabled: Boolean,
    scope: CoroutineScope,
) {
    val value = thumb.variable.value
    Box(
        Modifier.semantics {
            description?.let { contentDescription = it }
            stateDescription = value.toString()
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = value.toFloat(),
                range = range.minValue..range.maxValue,
            )
            // A disabled node publishes no set progress action, but a service may still perform it.
            setProgress { target -> isEnabled && thumb.setAccessibilityValue(target, range, scope) }
            if (!isEnabled) disabled()
        }
    )
}

private fun SliderThumb.setAccessibilityValue(
    target: Float,
    range: SliderRange,
    scope: CoroutineScope,
): Boolean {
    if (target.isNaN()) return false
    val current = variable.value
    val delta = target.coerceIn(range.minValue, range.maxValue) - current
    if (delta == 0f) return false
    val step = abs(delta).roundToLong().coerceAtLeast(1)
    setValue((current + sign(delta).toLong() * step).toFloat(), animated = false, scope = scope)
    return true
}

private fun joinDescription(description: String?, suffix: String): String =
    if (description == null) suffix else "$description,$suffix"
