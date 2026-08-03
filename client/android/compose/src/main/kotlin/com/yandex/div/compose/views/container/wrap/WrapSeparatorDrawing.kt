package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.mirrorHorizontallyIfRtl
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.container.SeparatorVisibility
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape

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
) {
    val totalWidthDp: Dp get() = shapeWidthDp + marginStartDp + marginEndDp
    val totalHeightDp: Dp get() = shapeHeightDp + marginTopDp + marginBottomDp
}

internal fun SeparatorDrawInfo.mainAxisTotalDp(isHorizontal: Boolean): Dp =
    if (isHorizontal) totalWidthDp else totalHeightDp

internal fun SeparatorDrawInfo.crossAxisTotalDp(isHorizontal: Boolean): Dp =
    if (isHorizontal) totalHeightDp else totalWidthDp

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
) {
    val totalWidth: Float
        get() = shapeWidth + marginStart + marginEnd
    val totalHeight: Float
        get() = shapeHeight + marginTop + marginBottom
}

internal fun SeparatorDrawInfo.toPx(density: Density): SeparatorDrawInfoPx = with(density) {
    SeparatorDrawInfoPx(
        color = color,
        shapeWidth = shapeWidthDp.toPx(),
        shapeHeight = shapeHeightDp.toPx(),
        cornerRadius = cornerRadiusDp.toPx(),
        marginStart = marginStartDp.toPx(),
        marginEnd = marginEndDp.toPx(),
        marginTop = marginTopDp.toPx(),
        marginBottom = marginBottomDp.toPx(),
        isCircle = isCircle,
    )
}

@Composable
internal fun DivContainer.Separator?.resolveDrawInfo(): SeparatorDrawInfo? {
    val sep = this ?: return null
    val style = sep.style as? DivDrawable.Shape ?: return null
    val shapeDrawable = style.value
    val fallbackColor = shapeDrawable.color?.observedColorValue()
    val layoutDirection = LocalLayoutDirection.current
    val margins = sep.margins.observeInsets()
    val marginStartDp = margins.calculateStartPadding(layoutDirection)
    val marginEndDp = margins.calculateEndPadding(layoutDirection)
    val marginTopDp = margins.calculateTopPadding()
    val marginBottomDp = margins.calculateBottomPadding()

    return when (val shape = shapeDrawable.shape) {
        is DivShape.RoundedRectangle -> {
            val rect = shape.value
            val color = rect.backgroundColor?.observedColorValue() ?: fallbackColor
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
            )
        }

        is DivShape.Circle -> {
            val circle = shape.value
            val radius = circle.radius.observedValue()
            val diameter = radius * 2
            val color = circle.backgroundColor?.observedColorValue() ?: fallbackColor
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
            )
        }
    }
}

internal fun DrawScope.drawHorizontalWrapSeparators(
    geometry: WrapSeparatorGeometry,
    sepInfo: SeparatorDrawInfoPx?,
    sepVisibility: SeparatorVisibility,
    lineSepInfo: SeparatorDrawInfoPx?,
    lineSepVisibility: SeparatorVisibility,
    edgeSepPadStartPx: Float,
    edgeSepPadEndPx: Float,
    edgeLineSepPadTopPx: Float,
    edgeLineSepPadBottomPx: Float,
    isRtl: Boolean,
) {
    geometry.prepare(size.width, isRtl)
    if (geometry.lineCount == 0) return

    mirrorHorizontallyIfRtl(isRtl) {
        if (lineSepVisibility.showAtStart && lineSepInfo != null) {
            val lineTop = geometry.lineAt(0).crossAxisMin
            drawLineSeparatorH(
                lineSepInfo,
                separatorBottom = lineTop - edgeLineSepPadTopPx + lineSepInfo.totalHeight,
                contentLeft = -edgeSepPadStartPx,
                contentRight = size.width + edgeSepPadEndPx,
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
                    contentLeft = -edgeSepPadStartPx,
                    contentRight = size.width + edgeSepPadEndPx,
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
                                    (edgeSepPadStartPx - sepInfo.totalWidth) / 2,
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
                        (edgeSepPadEndPx - sepInfo.totalWidth) / 2,
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
                separatorBottom = lineBottom + edgeLineSepPadBottomPx -
                    (edgeLineSepPadBottomPx - lineSepInfo.totalHeight) / 2,
                contentLeft = -edgeSepPadStartPx,
                contentRight = size.width + edgeSepPadEndPx,
            )
        }
    }
}

internal fun DrawScope.drawVerticalWrapSeparators(
    geometry: WrapSeparatorGeometry,
    sepInfo: SeparatorDrawInfoPx?,
    sepVisibility: SeparatorVisibility,
    lineSepInfo: SeparatorDrawInfoPx?,
    lineSepVisibility: SeparatorVisibility,
    edgeSepPadStartPx: Float,
    edgeSepPadEndPx: Float,
    edgeLineSepPadStartPx: Float,
    edgeLineSepPadEndPx: Float,
    isRtl: Boolean,
) {
    geometry.prepare(size.width, isRtl)
    if (geometry.lineCount == 0) return

    mirrorHorizontallyIfRtl(isRtl) {
        if (lineSepVisibility.showAtStart && lineSepInfo != null) {
            val lineLeft = geometry.lineAt(0).crossAxisMin
            drawLineSeparatorV(
                lineSepInfo,
                separatorRight = lineLeft - edgeLineSepPadStartPx + lineSepInfo.totalWidth,
                contentTop = -edgeSepPadStartPx,
                contentBottom = size.height + edgeSepPadEndPx,
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
                    contentTop = -edgeSepPadStartPx,
                    contentBottom = size.height + edgeSepPadEndPx,
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
                                    (edgeSepPadStartPx - sepInfo.totalHeight) / 2,
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
                        (edgeSepPadEndPx - sepInfo.totalHeight) / 2,
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
                separatorRight = lineRight + edgeLineSepPadEndPx -
                    (edgeLineSepPadEndPx - lineSepInfo.totalWidth) / 2,
                contentTop = -edgeSepPadStartPx,
                contentBottom = size.height + edgeSepPadEndPx,
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
}
