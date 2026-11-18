package com.yandex.div.compose.views.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import kotlin.math.ceil
import kotlin.math.roundToInt

internal data class ShapeParams(
    val width: Float,
    val height: Float,
    val cornerRadius: Float,
    val color: Color,
    val isCircle: Boolean,
    val strokeWidth: Float = 0f,
    val strokeColor: Color = Color.Transparent,
) {
    val layoutWidth: Float get() = ceil(width)
    val layoutHeight: Float get() = ceil(height)

    fun withStroke(): ShapeParams = if (strokeWidth == 0f) this else copy(
        width = width + strokeWidth,
        height = height + strokeWidth,
    )

    fun scale(factor: Float): ShapeParams = copy(
        width = width * factor,
        height = height * factor,
        cornerRadius = cornerRadius * factor,
    )
}

@Composable
internal fun DivIndicator.observeInactiveShape(
    activeItemSize: Float,
    minimumItemSize: Float,
    defaultColor: Color,
): ShapeParams {
    inactiveShape?.let { return it.toShapeParams(defaultColor) }
    activeShape?.let { return it.toShapeParams(defaultColor).scale(1f / activeItemSize) }
    inactiveMinimumShape?.let { return it.toShapeParams(defaultColor).scale(minimumItemSize) }
    return shape.toShapeParams(defaultColor)
}

@Composable
private fun DivShape.toShapeParams(fallbackColor: Color): ShapeParams = when (this) {
    is DivShape.RoundedRectangle -> value.toShapeParams(fallbackColor)
    is DivShape.Circle -> value.toShapeParams(fallbackColor)
}

@Composable
internal fun DivRoundedRectangleShape.toShapeParams(fallbackColor: Color): ShapeParams {
    val stroke = stroke
    val strokeWidthPx: Float
    val strokeColorPx: Color
    if (stroke != null) {
        strokeWidthPx = stroke.width.observedPxValue(stroke.unit).roundToInt().toFloat()
        strokeColorPx = stroke.color.observedColorValue()
    } else {
        strokeWidthPx = 0f
        strokeColorPx = Color.Transparent
    }
    return ShapeParams(
        width = itemWidth.value.observedPxValue(itemWidth.unit),
        height = itemHeight.value.observedPxValue(itemHeight.unit),
        cornerRadius = cornerRadius.value.observedPxValue(cornerRadius.unit),
        color = backgroundColor?.observedColorValue() ?: fallbackColor,
        isCircle = false,
        strokeWidth = strokeWidthPx,
        strokeColor = strokeColorPx,
    )
}

@Composable
private fun DivCircleShape.toShapeParams(fallbackColor: Color): ShapeParams {
    val radiusPx = radius.value.observedPxValue(radius.unit)
    return ShapeParams(
        width = radiusPx * 2,
        height = radiusPx * 2,
        cornerRadius = radiusPx,
        color = backgroundColor?.observedColorValue() ?: fallbackColor,
        isCircle = true,
    )
}
