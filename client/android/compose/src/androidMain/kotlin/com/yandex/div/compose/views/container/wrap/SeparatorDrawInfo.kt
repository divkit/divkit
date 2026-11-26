package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.views.container.SeparatorVisibility
import kotlin.math.ceil
import kotlin.math.min

@Immutable
internal data class SeparatorDrawInfo(
    val visibility: SeparatorVisibility,
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
    /** The size the separator takes along the given axis, margins included. */
    fun totalSize(alongWidth: Boolean): Dp = if (alongWidth) {
        shapeWidthDp + marginStartDp + marginEndDp
    } else {
        shapeHeightDp + marginTopDp + marginBottomDp
    }

    /** The size of the separator between two children, or null when it is not shown there. */
    fun betweenSize(alongWidth: Boolean): Dp? = if (visibility.showBetween) totalSize(alongWidth) else null

    /** The size of the separator at an edge of the container, zero when it is not shown there. */
    fun edgeSize(atStart: Boolean, alongWidth: Boolean): Dp {
        val isShown = if (atStart) visibility.showAtStart else visibility.showAtEnd
        return if (isShown) totalSize(alongWidth) else 0.dp
    }
}

@Immutable
internal data class SeparatorDrawInfoPx(
    val visibility: SeparatorVisibility,
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

    fun totalSize(alongWidth: Boolean): Float = if (alongWidth) totalWidth else totalHeight
}

internal fun SeparatorDrawInfo.toPx(density: Density): SeparatorDrawInfoPx = with(density) {
    val shapeWidth = shapeWidthDp.toPx()
    val shapeHeight = shapeHeightDp.toPx()
    return SeparatorDrawInfoPx(
        visibility = visibility,
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
