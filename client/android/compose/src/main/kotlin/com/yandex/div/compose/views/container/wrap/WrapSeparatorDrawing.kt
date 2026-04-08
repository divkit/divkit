package com.yandex.div.compose.views.container.wrap

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.views.container.SeparatorVisibility
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape

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

@Composable
internal fun DivContainer.Separator?.resolveDrawInfo(): SeparatorDrawInfo? {
    val sep = this ?: return null
    val style = sep.style as? DivDrawable.Shape ?: return null
    val shapeDrawable = style.value
    val color = shapeDrawable.color.observedColorValue()
    val margins = sep.margins

    val marginStartDp = margins?.run { (start ?: left).observedValue().toDp() } ?: 0.dp
    val marginEndDp = margins?.run { (end ?: right).observedValue().toDp() } ?: 0.dp
    val marginTopDp = margins?.top.observedValue(0L).toDp()
    val marginBottomDp = margins?.bottom.observedValue(0L).toDp()

    return when (val shape = shapeDrawable.shape) {
        is DivShape.RoundedRectangle -> {
            val rect = shape.value
            SeparatorDrawInfo(
                color = color,
                shapeWidthDp = rect.itemWidth.value.observedValue().toDp(),
                shapeHeightDp = rect.itemHeight.value.observedValue().toDp(),
                cornerRadiusDp = rect.cornerRadius.value.observedValue().toDp(),
                marginStartDp = marginStartDp,
                marginEndDp = marginEndDp,
                marginTopDp = marginTopDp,
                marginBottomDp = marginBottomDp,
                isCircle = false,
            )
        }

        is DivShape.Circle -> {
            val radius = shape.value.radius.value.observedValue().toDp()
            val diameter = radius * 2
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

private data class WrapLine(
    val firstIndex: Int,
    val lastIndex: Int,
    val crossAxisMin: Float,
    val crossAxisMax: Float,
)

private fun groupIntoHorizontalLines(rects: List<Rect>): List<WrapLine> =
    groupIntoLines(rects, isHorizontal = true)

private fun groupIntoVerticalLines(rects: List<Rect>): List<WrapLine> =
    groupIntoLines(rects, isHorizontal = false)

private fun groupIntoLines(rects: List<Rect>, isHorizontal: Boolean): List<WrapLine> {
    if (rects.isEmpty()) return emptyList()

    val lines = mutableListOf<WrapLine>()
    var lineStart = 0
    var crossMin = if (isHorizontal) rects[0].top else rects[0].left
    var crossMax = if (isHorizontal) rects[0].bottom else rects[0].right

    for (i in 1 until rects.size) {
        val rect = rects[i]
        if (rect == Rect.Zero) continue

        val crossStart = if (isHorizontal) rect.top else rect.left
        val crossEnd = if (isHorizontal) rect.bottom else rect.right

        if (crossStart >= crossMax) {
            lines.add(WrapLine(lineStart, i - 1, crossMin, crossMax))
            lineStart = i
            crossMin = crossStart
            crossMax = crossEnd
        } else {
            crossMin = minOf(crossMin, crossStart)
            crossMax = maxOf(crossMax, crossEnd)
        }
    }

    lines.add(WrapLine(lineStart, rects.lastIndex, crossMin, crossMax))
    return lines
}

internal fun DrawScope.drawHorizontalWrapSeparators(
    childRects: List<Rect>,
    sepInfo: SeparatorDrawInfo?,
    sepVisibility: SeparatorVisibility,
    lineSepInfo: SeparatorDrawInfo?,
    lineSepVisibility: SeparatorVisibility,
    edgeSepPadStartPx: Float,
    edgeSepPadEndPx: Float,
    edgeLineSepPadTopPx: Float,
    edgeLineSepPadBottomPx: Float,
) {
    if (childRects.isEmpty()) return
    val lines = groupIntoHorizontalLines(childRects)
    if (lines.isEmpty()) return

    if (lineSepVisibility.showAtStart && lineSepInfo != null) {
        val lineTop = lines.first().crossAxisMin
        drawLineSeparatorH(
            lineSepInfo,
            separatorBottom = lineTop - edgeLineSepPadTopPx + lineSepInfo.totalHeightDp.toPx(),
            contentLeft = -edgeSepPadStartPx,
            contentRight = size.width + edgeSepPadEndPx,
        )
    }

    lines.forEachIndexed { lineIdx, line ->
        if (lineIdx > 0 && lineSepVisibility.showBetween && lineSepInfo != null) {
            val prevLine = lines[lineIdx - 1]
            val gapTop = prevLine.crossAxisMax
            val gapBottom = line.crossAxisMin
            val gapCenter = (gapTop + gapBottom) / 2
            val sepHalfHeight = lineSepInfo.totalHeightDp.toPx() / 2
            drawLineSeparatorH(
                lineSepInfo,
                separatorBottom = gapCenter + sepHalfHeight,
                contentLeft = -edgeSepPadStartPx,
                contentRight = size.width + edgeSepPadEndPx,
            )
        }

        val lineTop = line.crossAxisMin
        val lineBottom = line.crossAxisMax

        for (i in line.firstIndex..line.lastIndex) {
            val rect = childRects[i]
            if (rect == Rect.Zero) continue

            if (i == line.firstIndex && sepVisibility.showAtStart && sepInfo != null) {
                drawItemSeparatorH(
                    sepInfo,
                    separatorRight = rect.left - (edgeSepPadStartPx - sepInfo.totalWidthDp.toPx()) / 2,
                    lineTop = lineTop,
                    lineBottom = lineBottom,
                )
            }

            if (i > line.firstIndex && sepVisibility.showBetween && sepInfo != null) {
                val prevRect = childRects[i - 1]
                val gapCenter = (prevRect.right + rect.left) / 2
                val sepHalfWidth = sepInfo.totalWidthDp.toPx() / 2
                drawItemSeparatorH(
                    sepInfo,
                    separatorRight = gapCenter + sepHalfWidth,
                    lineTop = lineTop,
                    lineBottom = lineBottom,
                )
            }

            if (i == line.lastIndex && sepVisibility.showAtEnd && sepInfo != null) {
                drawItemSeparatorH(
                    sepInfo,
                    separatorRight = rect.right + sepInfo.totalWidthDp.toPx() +
                        (edgeSepPadEndPx - sepInfo.totalWidthDp.toPx()) / 2,
                    lineTop = lineTop,
                    lineBottom = lineBottom,
                )
            }
        }
    }

    if (lineSepVisibility.showAtEnd && lineSepInfo != null) {
        val lineBottom = lines.last().crossAxisMax
        drawLineSeparatorH(
            lineSepInfo,
            separatorBottom = lineBottom + edgeLineSepPadBottomPx -
                (edgeLineSepPadBottomPx - lineSepInfo.totalHeightDp.toPx()) / 2,
            contentLeft = -edgeSepPadStartPx,
            contentRight = size.width + edgeSepPadEndPx,
        )
    }
}

internal fun DrawScope.drawVerticalWrapSeparators(
    childRects: List<Rect>,
    sepInfo: SeparatorDrawInfo?,
    sepVisibility: SeparatorVisibility,
    lineSepInfo: SeparatorDrawInfo?,
    lineSepVisibility: SeparatorVisibility,
    edgeSepPadStartPx: Float,
    edgeSepPadEndPx: Float,
    edgeLineSepPadStartPx: Float,
    edgeLineSepPadEndPx: Float,
) {
    if (childRects.isEmpty()) return
    val lines = groupIntoVerticalLines(childRects)
    if (lines.isEmpty()) return

    if (lineSepVisibility.showAtStart && lineSepInfo != null) {
        val lineLeft = lines.first().crossAxisMin
        drawLineSeparatorV(
            lineSepInfo,
            separatorRight = lineLeft - edgeLineSepPadStartPx + lineSepInfo.totalWidthDp.toPx(),
            contentTop = -edgeSepPadStartPx,
            contentBottom = size.height + edgeSepPadEndPx,
        )
    }

    lines.forEachIndexed { lineIdx, line ->
        if (lineIdx > 0 && lineSepVisibility.showBetween && lineSepInfo != null) {
            val prevLine = lines[lineIdx - 1]
            val gapLeft = prevLine.crossAxisMax
            val gapRight = line.crossAxisMin
            val gapCenter = (gapLeft + gapRight) / 2
            val sepHalfWidth = lineSepInfo.totalWidthDp.toPx() / 2
            drawLineSeparatorV(
                lineSepInfo,
                separatorRight = gapCenter + sepHalfWidth,
                contentTop = -edgeSepPadStartPx,
                contentBottom = size.height + edgeSepPadEndPx,
            )
        }

        val lineLeft = line.crossAxisMin
        val lineRight = line.crossAxisMax

        for (i in line.firstIndex..line.lastIndex) {
            val rect = childRects[i]
            if (rect == Rect.Zero) continue

            if (i == line.firstIndex && sepVisibility.showAtStart && sepInfo != null) {
                drawItemSeparatorV(
                    sepInfo,
                    separatorBottom = rect.top - (edgeSepPadStartPx - sepInfo.totalHeightDp.toPx()) / 2,
                    lineLeft = lineLeft,
                    lineRight = lineRight,
                )
            }

            if (i > line.firstIndex && sepVisibility.showBetween && sepInfo != null) {
                val prevRect = childRects[i - 1]
                val gapCenter = (prevRect.bottom + rect.top) / 2
                val sepHalfHeight = sepInfo.totalHeightDp.toPx() / 2
                drawItemSeparatorV(
                    sepInfo,
                    separatorBottom = gapCenter + sepHalfHeight,
                    lineLeft = lineLeft,
                    lineRight = lineRight,
                )
            }

            if (i == line.lastIndex && sepVisibility.showAtEnd && sepInfo != null) {
                drawItemSeparatorV(
                    sepInfo,
                    separatorBottom = rect.bottom + sepInfo.totalHeightDp.toPx() +
                        (edgeSepPadEndPx - sepInfo.totalHeightDp.toPx()) / 2,
                    lineLeft = lineLeft,
                    lineRight = lineRight,
                )
            }
        }
    }

    if (lineSepVisibility.showAtEnd && lineSepInfo != null) {
        val lineRight = lines.last().crossAxisMax
        drawLineSeparatorV(
            lineSepInfo,
            separatorRight = lineRight + edgeLineSepPadEndPx -
                (edgeLineSepPadEndPx - lineSepInfo.totalWidthDp.toPx()) / 2,
            contentTop = -edgeSepPadStartPx,
            contentBottom = size.height + edgeSepPadEndPx,
        )
    }
}

private fun DrawScope.drawItemSeparatorH(
    info: SeparatorDrawInfo,
    separatorRight: Float,
    lineTop: Float,
    lineBottom: Float,
) {
    val totalW = info.totalWidthDp.toPx()
    val marginStartPx = info.marginStartDp.toPx()
    val marginTopPx = info.marginTopDp.toPx()
    val marginBottomPx = info.marginBottomDp.toPx()
    val shapeW = info.shapeWidthDp.toPx()
    val shapeH = info.shapeHeightDp.toPx()

    val areaLeft = separatorRight - totalW + marginStartPx
    val areaTop = lineTop - marginTopPx
    val areaBottom = lineBottom + marginBottomPx
    val centerX = areaLeft + shapeW / 2
    val centerY = (areaTop + areaBottom) / 2

    drawSeparatorShape(info, centerX, centerY, shapeW, shapeH)
}

private fun DrawScope.drawItemSeparatorV(
    info: SeparatorDrawInfo,
    separatorBottom: Float,
    lineLeft: Float,
    lineRight: Float,
) {
    val totalH = info.totalHeightDp.toPx()
    val marginTopPx = info.marginTopDp.toPx()
    val marginStartPx = info.marginStartDp.toPx()
    val marginEndPx = info.marginEndDp.toPx()
    val shapeW = info.shapeWidthDp.toPx()
    val shapeH = info.shapeHeightDp.toPx()

    val areaTop = separatorBottom - totalH + marginTopPx
    val areaLeft = lineLeft - marginStartPx
    val areaRight = lineRight + marginEndPx
    val centerX = (areaLeft + areaRight) / 2
    val centerY = areaTop + shapeH / 2

    drawSeparatorShape(info, centerX, centerY, shapeW, shapeH)
}

private fun DrawScope.drawLineSeparatorH(
    info: SeparatorDrawInfo,
    separatorBottom: Float,
    contentLeft: Float,
    contentRight: Float,
) {
    val totalH = info.totalHeightDp.toPx()
    val marginTopPx = info.marginTopDp.toPx()
    val marginStartPx = info.marginStartDp.toPx()
    val marginEndPx = info.marginEndDp.toPx()
    val shapeW = info.shapeWidthDp.toPx()
    val shapeH = info.shapeHeightDp.toPx()

    val areaLeft = contentLeft + marginStartPx
    val areaRight = contentRight - marginEndPx
    val areaTop = separatorBottom - totalH + marginTopPx
    val centerX = (areaLeft + areaRight) / 2
    val centerY = areaTop + shapeH / 2

    drawSeparatorShape(info, centerX, centerY, shapeW, shapeH)
}

private fun DrawScope.drawLineSeparatorV(
    info: SeparatorDrawInfo,
    separatorRight: Float,
    contentTop: Float,
    contentBottom: Float,
) {
    val totalW = info.totalWidthDp.toPx()
    val marginStartPx = info.marginStartDp.toPx()
    val marginTopPx = info.marginTopDp.toPx()
    val marginBottomPx = info.marginBottomDp.toPx()
    val shapeW = info.shapeWidthDp.toPx()
    val shapeH = info.shapeHeightDp.toPx()

    val areaLeft = separatorRight - totalW + marginStartPx
    val areaTop = contentTop + marginTopPx
    val areaBottom = contentBottom - marginBottomPx
    val centerX = areaLeft + shapeW / 2
    val centerY = (areaTop + areaBottom) / 2

    drawSeparatorShape(info, centerX, centerY, shapeW, shapeH)
}

private fun DrawScope.drawSeparatorShape(
    info: SeparatorDrawInfo,
    centerX: Float,
    centerY: Float,
    shapeW: Float,
    shapeH: Float,
) {
    if (info.isCircle) {
        drawCircle(
            color = info.color,
            radius = shapeW / 2,
            center = Offset(centerX, centerY),
        )
    } else {
        drawRoundRect(
            color = info.color,
            topLeft = Offset(centerX - shapeW / 2, centerY - shapeH / 2),
            size = Size(shapeW, shapeH),
            cornerRadius = CornerRadius(info.cornerRadiusDp.toPx()),
        )
    }
}
