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
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape
import kotlin.math.min

@Composable
internal fun DivContainer.Separator?.resolveDrawInfo(): SeparatorDrawInfo? {
    val sep = this ?: return null
    val style = sep.style as? DivDrawable.Shape ?: return null
    val shapeDrawable = style.value
    val layoutDirection = LocalLayoutDirection.current
    val margins = sep.margins.observeInsets()
    val marginStartDp = margins.calculateStartPadding(layoutDirection)
    val marginEndDp = margins.calculateEndPadding(layoutDirection)
    val marginTopDp = margins.calculateTopPadding()
    val marginBottomDp = margins.calculateBottomPadding()

    return when (val shape = shapeDrawable.shape) {
        is DivShape.RoundedRectangle -> {
            val rect = shape.value
            val color = (rect.backgroundColor ?: shapeDrawable.color)?.observedColorValue()
            if (color == null) {
                reportError("Separator color not defined")
                return null
            }
            SeparatorDrawInfo(
                color = color,
                shapeWidthDp = rect.itemWidth.observedValue(),
                shapeHeightDp = rect.itemHeight.observedValue(),
                cornerRadiusDp = rect.cornerRadius.observedValue(),
                marginStartDp = marginStartDp,
                marginEndDp = marginEndDp,
                marginTopDp = marginTopDp,
                marginBottomDp = marginBottomDp,
                isCircle = false,
                stroke = (rect.stroke ?: shapeDrawable.stroke)?.observeSeparatorStroke(),
            )
        }

        is DivShape.Circle -> {
            val circle = shape.value
            val radius = circle.radius.observedValue()
            val diameter = radius * 2
            val color = (circle.backgroundColor ?: shapeDrawable.color)?.observedColorValue()
            if (color == null) {
                reportError("Separator color not defined")
                return null
            }
            SeparatorDrawInfo(
                color = color,
                shapeWidthDp = diameter,
                shapeHeightDp = diameter,
                cornerRadiusDp = radius,
                marginStartDp = marginStartDp,
                marginEndDp = marginEndDp,
                marginTopDp = marginTopDp,
                marginBottomDp = marginBottomDp,
                isCircle = true,
                stroke = (circle.stroke ?: shapeDrawable.stroke)?.observeSeparatorStroke(),
            )
        }
    }
}

internal fun DrawScope.drawHorizontalWrapSeparators(
    geometry: WrapSeparatorGeometry,
    params: WrapSeparatorDrawParams,
    isRtl: Boolean,
) {
    val sepInfo = params.itemSeparatorInfo
    val sepVisibility = params.itemSeparatorVisibility
    val lineSepInfo = params.lineSeparatorInfo
    val lineSepVisibility = params.lineSeparatorVisibility
    geometry.prepare(size.width, isRtl)
    if (geometry.lineCount == 0) return

    mirrorHorizontallyIfRtl(isRtl) {
        if (lineSepVisibility.showAtStart && lineSepInfo != null) {
            val lineTop = geometry.lineAt(0).crossAxisMin
            drawLineSeparatorH(
                lineSepInfo,
                separatorBottom = lineTop - params.crossAxisStartPadding + lineSepInfo.totalHeight,
                contentLeft = -params.mainAxisStartPadding,
                contentRight = size.width + params.mainAxisEndPadding,
            )
        }

        var lineIndex = 0
        while (lineIndex < geometry.lineCount) {
            val line = geometry.lineAt(lineIndex)
            if (lineIndex > 0 && lineSepVisibility.showBetween && lineSepInfo != null) {
                val prevLine = geometry.lineAt(lineIndex - 1)
                val gapTop = prevLine.crossAxisMax
                val gapBottom = line.crossAxisMin
                val gapCenter = (gapTop + gapBottom) / 2
                drawLineSeparatorH(
                    lineSepInfo,
                    separatorBottom = gapCenter + lineSepInfo.totalHeight / 2,
                    contentLeft = -params.mainAxisStartPadding,
                    contentRight = size.width + params.mainAxisEndPadding,
                )
            }

            val lineTop = line.crossAxisMin
            val lineBottom = line.crossAxisMax

            var previousIndex = -1
            var index = line.firstIndex
            while (index <= line.lastIndex) {
                if (geometry.isChildPlaced(index)) {
                    val rect = geometry.childRectAt(index)
                    val rectLeft = geometry.logicalLeft(rect, size.width, isRtl)

                    if (previousIndex < 0) {
                        if (sepVisibility.showAtStart && sepInfo != null) {
                            drawItemSeparatorH(
                                sepInfo,
                                separatorRight = rectLeft -
                                    (params.mainAxisStartPadding - sepInfo.totalWidth) / 2,
                                lineTop = lineTop,
                                lineBottom = lineBottom,
                            )
                        }
                    } else if (sepVisibility.showBetween && sepInfo != null) {
                        val previousRect = geometry.childRectAt(previousIndex)
                        val previousRight = geometry.logicalRight(previousRect, size.width, isRtl)
                        val gapCenter = (previousRight + rectLeft) / 2
                        drawItemSeparatorH(
                            sepInfo,
                            separatorRight = gapCenter + sepInfo.totalWidth / 2,
                            lineTop = lineTop,
                            lineBottom = lineBottom,
                        )
                    }
                    previousIndex = index
                }
                index++
            }

            if (previousIndex >= 0 && sepVisibility.showAtEnd && sepInfo != null) {
                val lastRect = geometry.childRectAt(previousIndex)
                val lastRight = geometry.logicalRight(lastRect, size.width, isRtl)
                drawItemSeparatorH(
                    sepInfo,
                    separatorRight = lastRight + sepInfo.totalWidth +
                        (params.mainAxisEndPadding - sepInfo.totalWidth) / 2,
                    lineTop = lineTop,
                    lineBottom = lineBottom,
                )
            }
            lineIndex++
        }

        if (lineSepVisibility.showAtEnd && lineSepInfo != null) {
            val lineBottom = geometry.lineAt(geometry.lineCount - 1).crossAxisMax
            drawLineSeparatorH(
                lineSepInfo,
                separatorBottom = lineBottom + params.crossAxisEndPadding -
                    (params.crossAxisEndPadding - lineSepInfo.totalHeight) / 2,
                contentLeft = -params.mainAxisStartPadding,
                contentRight = size.width + params.mainAxisEndPadding,
            )
        }
    }
}

internal fun DrawScope.drawVerticalWrapSeparators(
    geometry: WrapSeparatorGeometry,
    params: WrapSeparatorDrawParams,
    isRtl: Boolean,
) {
    val sepInfo = params.itemSeparatorInfo
    val sepVisibility = params.itemSeparatorVisibility
    val lineSepInfo = params.lineSeparatorInfo
    val lineSepVisibility = params.lineSeparatorVisibility
    geometry.prepare(size.width, isRtl)
    if (geometry.lineCount == 0) return

    mirrorHorizontallyIfRtl(isRtl) {
        if (lineSepVisibility.showAtStart && lineSepInfo != null) {
            val lineLeft = geometry.lineAt(0).crossAxisMin
            drawLineSeparatorV(
                lineSepInfo,
                separatorRight = lineLeft - params.crossAxisStartPadding + lineSepInfo.totalWidth,
                contentTop = -params.mainAxisStartPadding,
                contentBottom = size.height + params.mainAxisEndPadding,
            )
        }

        var lineIndex = 0
        while (lineIndex < geometry.lineCount) {
            val line = geometry.lineAt(lineIndex)
            if (lineIndex > 0 && lineSepVisibility.showBetween && lineSepInfo != null) {
                val prevLine = geometry.lineAt(lineIndex - 1)
                val gapLeft = prevLine.crossAxisMax
                val gapRight = line.crossAxisMin
                val gapCenter = (gapLeft + gapRight) / 2
                drawLineSeparatorV(
                    lineSepInfo,
                    separatorRight = gapCenter + lineSepInfo.totalWidth / 2,
                    contentTop = -params.mainAxisStartPadding,
                    contentBottom = size.height + params.mainAxisEndPadding,
                )
            }

            val lineLeft = line.crossAxisMin
            val lineRight = line.crossAxisMax

            var previousIndex = -1
            var index = line.firstIndex
            while (index <= line.lastIndex) {
                if (geometry.isChildPlaced(index)) {
                    val rect = geometry.childRectAt(index)
                    if (previousIndex < 0) {
                        if (sepVisibility.showAtStart && sepInfo != null) {
                            drawItemSeparatorV(
                                sepInfo,
                                separatorBottom = rect.top -
                                    (params.mainAxisStartPadding - sepInfo.totalHeight) / 2,
                                lineLeft = lineLeft,
                                lineRight = lineRight,
                            )
                        }
                    } else if (sepVisibility.showBetween && sepInfo != null) {
                        val previousRect = geometry.childRectAt(previousIndex)
                        val gapCenter = (previousRect.bottom + rect.top) / 2
                        drawItemSeparatorV(
                            sepInfo,
                            separatorBottom = gapCenter + sepInfo.totalHeight / 2,
                            lineLeft = lineLeft,
                            lineRight = lineRight,
                        )
                    }
                    previousIndex = index
                }
                index++
            }

            if (previousIndex >= 0 && sepVisibility.showAtEnd && sepInfo != null) {
                val lastRect = geometry.childRectAt(previousIndex)
                drawItemSeparatorV(
                    sepInfo,
                    separatorBottom = lastRect.bottom + sepInfo.totalHeight +
                        (params.mainAxisEndPadding - sepInfo.totalHeight) / 2,
                    lineLeft = lineLeft,
                    lineRight = lineRight,
                )
            }
            lineIndex++
        }

        if (lineSepVisibility.showAtEnd && lineSepInfo != null) {
            val lineRight = geometry.lineAt(geometry.lineCount - 1).crossAxisMax
            drawLineSeparatorV(
                lineSepInfo,
                separatorRight = lineRight + params.crossAxisEndPadding -
                    (params.crossAxisEndPadding - lineSepInfo.totalWidth) / 2,
                contentTop = -params.mainAxisStartPadding,
                contentBottom = size.height + params.mainAxisEndPadding,
            )
        }
    }
}

private fun DrawScope.drawItemSeparatorH(
    info: SeparatorDrawInfoPx,
    separatorRight: Float,
    lineTop: Float,
    lineBottom: Float,
) {
    val areaLeft = separatorRight - info.totalWidth + info.marginStart
    val areaTop = lineTop - info.marginTop
    val areaBottom = lineBottom + info.marginBottom
    val centerX = areaLeft + info.shapeWidth / 2
    val centerY = (areaTop + areaBottom) / 2

    drawSeparatorShape(info, centerX, centerY)
}

private fun DrawScope.drawItemSeparatorV(
    info: SeparatorDrawInfoPx,
    separatorBottom: Float,
    lineLeft: Float,
    lineRight: Float,
) {
    val areaTop = separatorBottom - info.totalHeight + info.marginTop
    val areaLeft = lineLeft - info.marginStart
    val areaRight = lineRight + info.marginEnd
    val centerX = (areaLeft + areaRight) / 2
    val centerY = areaTop + info.shapeHeight / 2

    drawSeparatorShape(info, centerX, centerY)
}

private fun DrawScope.drawLineSeparatorH(
    info: SeparatorDrawInfoPx,
    separatorBottom: Float,
    contentLeft: Float,
    contentRight: Float,
) {
    val areaLeft = contentLeft + info.marginStart
    val areaRight = contentRight - info.marginEnd
    val areaTop = separatorBottom - info.totalHeight + info.marginTop
    val centerX = (areaLeft + areaRight) / 2
    val centerY = areaTop + info.shapeHeight / 2

    drawSeparatorShape(info, centerX, centerY)
}

private fun DrawScope.drawLineSeparatorV(
    info: SeparatorDrawInfoPx,
    separatorRight: Float,
    contentTop: Float,
    contentBottom: Float,
) {
    val areaLeft = separatorRight - info.totalWidth + info.marginStart
    val areaTop = contentTop + info.marginTop
    val areaBottom = contentBottom - info.marginBottom
    val centerX = areaLeft + info.shapeWidth / 2
    val centerY = (areaTop + areaBottom) / 2

    drawSeparatorShape(info, centerX, centerY)
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
