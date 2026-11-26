package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.mirrorHorizontallyIfRtl
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.container.observeSeparatorStroke
import com.yandex.div.compose.views.container.resolveSeparatorVisibility
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape
import kotlin.math.min

/** The separator to draw, or null when it is not shown anywhere. */
@Composable
internal fun DivContainer.Separator?.observeWrapSeparator(): SeparatorDrawInfo? {
    val visibility = resolveSeparatorVisibility()
    if (this == null || !visibility.hasAnySeparator) return null
    val drawable = (style as? DivDrawable.Shape)?.value ?: return null
    val shape = drawable.shape
    val color = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.backgroundColor
        is DivShape.Circle -> shape.value.backgroundColor
    } ?: drawable.color
    if (color == null) {
        reportError("Separator color not defined")
        return null
    }
    val stroke = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.stroke
        is DivShape.Circle -> shape.value.stroke
    } ?: drawable.stroke
    val radius = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.cornerRadius.observedValue()
        is DivShape.Circle -> shape.value.radius.observedValue()
    }
    val layoutDirection = LocalLayoutDirection.current
    val margins = margins.observeInsets()
    return SeparatorDrawInfo(
        visibility = visibility,
        color = color.observedColorValue(),
        shapeWidthDp = if (shape is DivShape.RoundedRectangle) shape.value.itemWidth.observedValue() else radius * 2,
        shapeHeightDp = if (shape is DivShape.RoundedRectangle) shape.value.itemHeight.observedValue() else radius * 2,
        cornerRadiusDp = radius,
        marginStartDp = margins.calculateStartPadding(layoutDirection),
        marginEndDp = margins.calculateEndPadding(layoutDirection),
        marginTopDp = margins.calculateTopPadding(),
        marginBottomDp = margins.calculateBottomPadding(),
        isCircle = shape is DivShape.Circle,
        stroke = stroke?.observeSeparatorStroke(),
    )
}

/**
 * Draws the separators of a wrap container over its content, as the other platforms do: item
 * separators in the gaps between the items of a line, centered across the line, then line separators
 * over them in the gaps between the lines, centered along the lines. Separators at the edges are
 * drawn in the padding reserved for them.
 */
internal fun DrawScope.drawWrapSeparators(
    geometry: WrapSeparatorGeometry,
    isHorizontal: Boolean,
    itemSeparator: SeparatorDrawInfoPx?,
    lineSeparator: SeparatorDrawInfoPx?,
    isRtl: Boolean,
) {
    geometry.prepare(size.width, isRtl)
    mirrorHorizontallyIfRtl(isRtl) {
        if (itemSeparator != null) {
            for (lineIndex in 0 until geometry.lineCount) {
                drawItemSeparators(geometry, geometry.lineAt(lineIndex), itemSeparator, isHorizontal, isRtl)
            }
        }
        if (lineSeparator != null && geometry.lineCount > 0) {
            drawLineSeparators(geometry, lineSeparator, isHorizontal)
        }
    }
}

private fun DrawScope.drawLineSeparators(
    geometry: WrapSeparatorGeometry,
    separator: SeparatorDrawInfoPx,
    isHorizontal: Boolean,
) {
    // Line separators are centered along the extent the lines take together.
    var mainMin = Float.POSITIVE_INFINITY
    var mainMax = Float.NEGATIVE_INFINITY
    for (lineIndex in 0 until geometry.lineCount) {
        val line = geometry.lineAt(lineIndex)
        mainMin = minOf(mainMin, line.mainAxisMin)
        mainMax = maxOf(mainMax, line.mainAxisMax)
    }
    val crossSize = separator.totalSize(alongWidth = !isHorizontal)
    val lastLine = geometry.lineAt(geometry.lineCount - 1)
    if (separator.visibility.showAtStart) {
        drawLineSeparator(separator, isHorizontal, crossEnd = geometry.lineAt(0).crossAxisMin, mainMin, mainMax)
    }
    if (separator.visibility.showBetween) {
        for (lineIndex in 1 until geometry.lineCount) {
            val gapCenter = (geometry.lineAt(lineIndex - 1).crossAxisMax + geometry.lineAt(lineIndex).crossAxisMin) / 2
            drawLineSeparator(separator, isHorizontal, crossEnd = gapCenter + crossSize / 2, mainMin, mainMax)
        }
    }
    if (separator.visibility.showAtEnd) {
        drawLineSeparator(separator, isHorizontal, crossEnd = lastLine.crossAxisMax + crossSize, mainMin, mainMax)
    }
}

private fun DrawScope.drawItemSeparators(
    geometry: WrapSeparatorGeometry,
    line: WrapLine,
    separator: SeparatorDrawInfoPx,
    isHorizontal: Boolean,
    isRtl: Boolean,
) {
    val mainSize = separator.totalSize(alongWidth = isHorizontal)
    // The end of the previous item placed in the line.
    var previousEnd = Float.NaN
    for (index in line.firstIndex..line.lastIndex) {
        if (!geometry.isChildPlaced(index)) continue
        val start = geometry.childMainStart(index, size.width, isRtl)
        if (previousEnd.isNaN()) {
            if (separator.visibility.showAtStart) drawItemSeparator(separator, isHorizontal, start, line)
        } else if (separator.visibility.showBetween) {
            drawItemSeparator(separator, isHorizontal, (previousEnd + start) / 2 + mainSize / 2, line)
        }
        previousEnd = geometry.childMainEnd(index, size.width, isRtl)
    }
    if (separator.visibility.showAtEnd && !previousEnd.isNaN()) {
        drawItemSeparator(separator, isHorizontal, previousEnd + mainSize, line)
    }
}

/**
 * Draws an item separator that ends at [mainEnd] along the line. Across the line it is centered
 * between its margins inside the line.
 */
private fun DrawScope.drawItemSeparator(
    info: SeparatorDrawInfoPx,
    isHorizontal: Boolean,
    mainEnd: Float,
    line: WrapLine,
) {
    if (isHorizontal) {
        val left = mainEnd - info.totalWidth + info.marginStart
        val top = line.crossAxisMin + info.marginTop
        val bottom = line.crossAxisMax - info.marginBottom
        drawSeparatorShape(info, centerX = left + info.shapeWidth / 2, centerY = (top + bottom) / 2)
    } else {
        val top = mainEnd - info.totalHeight + info.marginTop
        val left = line.crossAxisMin + info.marginStart
        val right = line.crossAxisMax - info.marginEnd
        drawSeparatorShape(info, centerX = (left + right) / 2, centerY = top + info.shapeHeight / 2)
    }
}

/**
 * Draws a line separator that ends at [crossEnd] across the lines, centered between its margins
 * inside [mainMin]..[mainMax], the extent of the lines.
 */
private fun DrawScope.drawLineSeparator(
    info: SeparatorDrawInfoPx,
    isHorizontal: Boolean,
    crossEnd: Float,
    mainMin: Float,
    mainMax: Float,
) {
    if (isHorizontal) {
        val left = mainMin + info.marginStart
        val right = mainMax - info.marginEnd
        val top = crossEnd - info.totalHeight + info.marginTop
        drawSeparatorShape(info, centerX = (left + right) / 2, centerY = top + info.shapeHeight / 2)
    } else {
        val left = crossEnd - info.totalWidth + info.marginStart
        val top = mainMin + info.marginTop
        val bottom = mainMax - info.marginBottom
        drawSeparatorShape(info, centerX = left + info.shapeWidth / 2, centerY = (top + bottom) / 2)
    }
}

private fun DrawScope.drawSeparatorShape(
    info: SeparatorDrawInfoPx,
    centerX: Float,
    centerY: Float,
) {
    if (info.isCircle) {
        drawCircle(
            color = info.color,
            radius = info.shapeWidth / 2,
            center = Offset(centerX, centerY),
        )
    } else {
        drawRoundRect(
            color = info.color,
            topLeft = Offset(
                centerX - info.shapeWidth / 2,
                centerY - info.shapeHeight / 2,
            ),
            size = Size(info.shapeWidth, info.shapeHeight),
            cornerRadius = CornerRadius(info.cornerRadius),
        )
    }
    val brush = info.strokeBrush ?: return
    val stroke = info.strokeStyle ?: return
    val minDimension = min(info.shapeWidth, info.shapeHeight)
    if (minDimension <= 0f) return
    if (info.isCircle) {
        // Keep the stroke centered on the circle contour, as in the View drawable.
        if (stroke.width >= info.shapeWidth) {
            drawCircle(
                brush = brush,
                radius = (info.shapeWidth + stroke.width) / 2,
                center = Offset(centerX, centerY),
            )
        } else {
            drawCircle(
                brush = brush,
                radius = info.shapeWidth / 2,
                center = Offset(centerX, centerY),
                style = stroke,
            )
        }
    } else {
        val inset = stroke.width / 2
        val fillArea = stroke.width * 2 >= minDimension
        val topLeft = Offset(centerX - info.shapeWidth / 2, centerY - info.shapeHeight / 2)
        val radius = min(info.cornerRadius, minDimension / 2)
        when {
            fillArea -> drawRoundRect(
                brush, topLeft, Size(info.shapeWidth, info.shapeHeight), CornerRadius(radius),
            )
            radius < inset -> clipRect(
                left = topLeft.x + stroke.width,
                top = topLeft.y + stroke.width,
                right = topLeft.x + info.shapeWidth - stroke.width,
                bottom = topLeft.y + info.shapeHeight - stroke.width,
                clipOp = ClipOp.Difference,
            ) {
                drawRoundRect(brush, topLeft, Size(info.shapeWidth, info.shapeHeight), CornerRadius(radius))
            }
            else -> drawRoundRect(
                brush = brush,
                topLeft = topLeft + Offset(inset, inset),
                size = Size(info.shapeWidth - stroke.width, info.shapeHeight - stroke.width),
                cornerRadius = CornerRadius(radius - inset),
                style = stroke,
            )
        }
    }
}
