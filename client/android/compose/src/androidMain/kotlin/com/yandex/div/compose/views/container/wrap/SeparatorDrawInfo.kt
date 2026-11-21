package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.ceil
import kotlin.math.min

@Immutable
internal data class SeparatorDrawInfo(
    val color: Color,
    val shapeWidthDp: Dp,
    val shapeHeightDp: Dp,
    val cornerRadiusDp: Dp,
    val marginStartDp: Dp,
    val marginEndDp: Dp,
    val marginTopDp: Dp,
    val marginBottomDp: Dp,
    val isCircle: Boolean,
    val stroke: BorderStroke?,
) {
    val totalWidthDp: Dp get() = shapeWidthDp + marginStartDp + marginEndDp
    val totalHeightDp: Dp get() = shapeHeightDp + marginTopDp + marginBottomDp
}

internal fun SeparatorDrawInfo.mainAxisTotalDp(isHorizontal: Boolean): Dp =
    if (isHorizontal) totalWidthDp else totalHeightDp

internal fun SeparatorDrawInfo.crossAxisTotalDp(isHorizontal: Boolean): Dp =
    if (isHorizontal) totalHeightDp else totalWidthDp

@Immutable
internal data class SeparatorDrawInfoPx(
    val color: Color,
    val shapeWidth: Float,
    val shapeHeight: Float,
    val cornerRadius: Float,
    val marginStart: Float,
    val marginEnd: Float,
    val marginTop: Float,
    val marginBottom: Float,
    val isCircle: Boolean,
    val strokeBrush: Brush?,
    val strokeStyle: Stroke?,
) {
    val totalWidth: Float
        get() = shapeWidth + marginStart + marginEnd

    val totalHeight: Float
        get() = shapeHeight + marginTop + marginBottom
}

internal fun SeparatorDrawInfo.toPx(density: Density): SeparatorDrawInfoPx = with(density) {
    val shapeWidth = shapeWidthDp.toPx()
    val shapeHeight = shapeHeightDp.toPx()
    return SeparatorDrawInfoPx(
        color = color,
        shapeWidth = shapeWidth,
        shapeHeight = shapeHeight,
        cornerRadius = cornerRadiusDp.toPx(),
        marginStart = marginStartDp.toPx(),
        marginEnd = marginEndDp.toPx(),
        marginTop = marginTopDp.toPx(),
        marginBottom = marginBottomDp.toPx(),
        isCircle = isCircle,
        strokeBrush = stroke?.brush,
        strokeStyle = stroke?.let {
            val width = if (isCircle) {
                it.width.toPx()
            } else {
                ceil(it.width.toPx())
                    .coerceAtLeast(1f)
                    .coerceAtMost(ceil(min(shapeWidth, shapeHeight) / 2))
            }
            Stroke(width)
        },
    )
}

