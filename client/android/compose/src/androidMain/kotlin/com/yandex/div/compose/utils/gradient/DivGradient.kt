package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Center
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Radius
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.render.common.gradient.GradientGeometry.RelativeRadius
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeRadius

@Composable
internal fun DivLinearGradient.observeLinearGradient(animationPhase: Float? = null): Brush? {
    return observedBrush(
        colorMap = colorMap?.map {
            it.position.observedFloatValue() to it.color.observedColorValue()
        },
        fallbackColors = colors,
    ) { colors, colorStops ->
        LinearGradientBrush(
            angle = angle.observedIntValue(),
            colors = colors,
            colorStops = colorStops,
            animationPhase = animationPhase,
        )
    }
}

@Composable
internal fun DivRadialGradient.observeRadialGradient(animationPhase: Float? = null): Brush? {
    return observedBrush(
        colorMap = colorMap?.map {
            it.position.observedFloatValue() to it.color.observedColorValue()
        },
        fallbackColors = colors,
    ) { colors, colorStops ->
        RadialGradientBrush(
            centerX = centerX.observedCenter(),
            centerY = centerY.observedCenter(),
            radius = radius.observedRadius(),
            colors = colors,
            colorStops = colorStops,
            animationPhase = animationPhase,
        )
    }
}

@Composable
private inline fun observedBrush(
    colorMap: List<Pair<Float, Color>>?,
    fallbackColors: ExpressionList<Int>?,
    createBrush: (List<Color>, List<Float>?) -> Brush,
): Brush? {
    if (colorMap != null) {
        val sorted = colorMap.sortedBy { it.first }
        return createBrush(sorted.map { it.second }, sorted.map { it.first })
    }

    val colors = fallbackColors?.observedValue()
        ?.map { it.toColor() }
        ?: return null
    return createBrush(colors, null)
}

@Composable
private fun DivRadialGradientCenter.observedCenter(): Center {
    return when (this) {
        is DivRadialGradientCenter.Fixed ->
            Center.Fixed(value.value.observedPxValue(value.unit))

        is DivRadialGradientCenter.Relative ->
            Center.Relative(value.value.observedFloatValue())
    }
}

@Composable
private fun DivRadialGradientRadius.observedRadius(): Radius {
    return when (this) {
        is DivRadialGradientRadius.FixedSize ->
            Radius.Fixed(value.observedValue().toPx())

        is DivRadialGradientRadius.Relative ->
            Radius.Relative(value.value.observedValue().toRelativeRadius())
    }
}

private fun DivRadialGradientRelativeRadius.Value.toRelativeRadius(): RelativeRadius {
    return when (this) {
        DivRadialGradientRelativeRadius.Value.NEAREST_CORNER -> RelativeRadius.NEAREST_CORNER
        DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER -> RelativeRadius.FARTHEST_CORNER
        DivRadialGradientRelativeRadius.Value.NEAREST_SIDE -> RelativeRadius.NEAREST_SIDE
        DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE -> RelativeRadius.FARTHEST_SIDE
    }
}
