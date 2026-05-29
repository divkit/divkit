package com.yandex.div.compose.views.indicator

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.pager.DivPagerState
import com.yandex.div2.DivIndicator

internal fun DrawScope.drawIndicators(style: IndicatorStyle, pagerState: DivPagerState?) {
    if (pagerState == null) return
    val itemsCount = pagerState.pageCount
    if (itemsCount <= 0) return

    val (activePage, offset) = normalizePagerPosition(
        rawPage = pagerState.currentPage,
        rawOffset = pagerState.currentPageOffsetFraction,
        itemsCount = itemsCount,
    )
    val viewportWidth = size.width
    val centerY = size.height / 2f

    val space: Float
    val widthScale: Float
    val maxVisible: Int
    if (style.isStretch) {
        maxVisible = style.maxVisibleItems.coerceIn(1, itemsCount)
        space = (viewportWidth + style.itemSpacing) / maxVisible
        widthScale = if (style.activeShape.width > 0f) {
            (space - style.itemSpacing) / style.activeShape.width
        } else 1f
    } else {
        space = style.spaceBetweenCenters
        widthScale = 1f
        maxVisible = if (space > 0f) {
            (viewportWidth / space).toInt().coerceIn(1, itemsCount)
        } else itemsCount
    }

    val shapes = Array(itemsCount) { i ->
        val base = if (style.animation == DivIndicator.Animation.SCALE) {
            interpolateScaleShape(style, i, activePage, offset, itemsCount)
        } else {
            style.inactiveShape
        }
        if (widthScale != 1f && !base.isCircle) base.copy(width = base.width * widthScale) else base
    }
    val positions = FloatArray(itemsCount).apply {
        this[0] = shapes[0].width / 2f
        for (i in 1 until itemsCount) this[i] = this[i - 1] + space
    }

    val shift = if (itemsCount <= maxVisible) {
        (viewportWidth - positions.last() - shapes.last().width / 2f) / 2f
    } else {
        val activeCenter = positions[activePage.coerceIn(0, itemsCount - 1)]
        val evenAdjust = if (maxVisible % 2 == 0) space / 2f else 0f
        viewportWidth / 2f - activeCenter - space * offset + evenAdjust
    }
    for (i in positions.indices) positions[i] += shift

    val visible = if (itemsCount <= maxVisible) {
        positions.indices.toList()
    } else {
        clipAndScaleEdges(style, positions, shapes, itemsCount, space, viewportWidth, activePage, offset)
    }

    for (i in visible) drawShape(positions[i], centerY, shapes[i])
    drawSelectionOverlay(style, positions, activePage, offset, centerY, viewportWidth, itemsCount)
}

internal fun normalizePagerPosition(rawPage: Int, rawOffset: Float, itemsCount: Int): Pair<Int, Float> {
    val page = rawPage.coerceIn(0, itemsCount - 1)
    return when {
        rawOffset == 0f -> page to 0f
        rawOffset < 0f -> page to (-rawOffset).coerceIn(0f, MAX_OFFSET)
        page == 0 -> 0 to 0f
        else -> (page - 1) to (1f - rawOffset).coerceIn(0f, MAX_OFFSET)
    }
}

private fun clipAndScaleEdges(
    style: IndicatorStyle,
    positions: FloatArray,
    shapes: Array<ShapeParams>,
    itemsCount: Int,
    space: Float,
    viewportWidth: Float,
    activePage: Int,
    offset: Float,
): List<Int> {
    val firstLeft = positions[0] - shapes[0].width / 2f
    val lastRight = positions[itemsCount - 1] + shapes[itemsCount - 1].width / 2f
    val extraShift = when {
        firstLeft in 0f..viewportWidth -> -firstLeft
        lastRight in 0f..viewportWidth -> viewportWidth - lastRight
        else -> 0f
    }
    if (extraShift != 0f) {
        for (i in positions.indices) positions[i] += extraShift
    }

    val visible = positions.indices.filter { positions[it] in 0f..viewportWidth }
    if (visible.isEmpty()) return visible

    val isBoundaryPage = offset == 0f && (activePage == 0 || activePage == itemsCount - 1)
    val preserveEdges = visible.size == 3 && !isBoundaryPage

    visible.forEachIndexed { index, page ->
        val isEdge = index == 0 || index == visible.lastIndex
        val isAnchor = page == activePage || page == 0 || page == itemsCount - 1
        if (isAnchor || (preserveEdges && isEdge)) return@forEachIndexed
        val fraction = edgeScaleFraction(positions[page], viewportWidth, space)
        if (fraction < 1f) shapes[page] = scaleEdgeItem(shapes[page], style.minimumShape, fraction)
    }
    return visible
}

private fun edgeScaleFraction(center: Float, viewportWidth: Float, space: Float): Float {
    if (space <= 0f) return 1f
    val distance = if (center <= space) center else (viewportWidth - center).coerceAtMost(space)
    return (distance / space).coerceIn(0f, 1f)
}

private fun scaleEdgeItem(item: ShapeParams, minimum: ShapeParams, fraction: Float): ShapeParams {
    val newWidth = item.width * fraction
    if (newWidth <= minimum.width) return minimum
    if (item.isCircle) {
        return item.copy(width = newWidth, height = newWidth, cornerRadius = newWidth / 2f)
    }
    val heightFactor = if (item.width > 0f) newWidth / item.width else 1f
    return item.copy(width = newWidth, height = item.height * heightFactor)
}

private fun interpolateScaleShape(
    style: IndicatorStyle,
    position: Int,
    activePosition: Int,
    offset: Float,
    itemsCount: Int,
): ShapeParams {
    val scale = when {
        position == activePosition -> 1f - offset
        position == activePosition + 1 -> offset
        position == 0 && activePosition == itemsCount - 1 -> offset
        else -> 0f
    }.coerceIn(0f, 1f)
    return when (scale) {
        0f -> style.inactiveShape
        1f -> style.activeShape
        else -> lerpShape(style.inactiveShape, style.activeShape, scale)
    }
}

private fun lerpShape(src: ShapeParams, dst: ShapeParams, fraction: Float): ShapeParams = ShapeParams(
    width = lerp(src.width + src.strokeWidth, dst.width + dst.strokeWidth, fraction),
    height = lerp(src.height + src.strokeWidth, dst.height + dst.strokeWidth, fraction),
    cornerRadius = lerp(src.cornerRadius, dst.cornerRadius, fraction),
    color = lerp(src.color, dst.color, fraction),
    isCircle = src.isCircle,
    strokeWidth = lerp(src.strokeWidth, dst.strokeWidth, fraction),
    strokeColor = lerp(src.strokeColor, dst.strokeColor, fraction),
)

private fun DrawScope.drawSelectionOverlay(
    style: IndicatorStyle,
    positions: FloatArray,
    activePosition: Int,
    offset: Float,
    centerY: Float,
    viewportWidth: Float,
    itemsCount: Int,
) {
    if (style.animation == DivIndicator.Animation.SCALE) return
    if (activePosition !in 0 until itemsCount) return

    val active = style.activeShape
    val halfWidth = active.width / 2f
    val anchor = positions[activePosition]
    val space = style.spaceBetweenCenters

    val (leftEdge, rightEdge) = when (style.animation) {
        DivIndicator.Animation.WORM ->
            (space * (offset - 0.5f) * 2f).coerceAtLeast(0f) to (space * offset * 2f).coerceAtMost(space)
        DivIndicator.Animation.SLIDER -> {
            val pos = space * offset
            pos.coerceAtLeast(0f) to pos.coerceAtMost(space)
        }
        DivIndicator.Animation.SCALE -> return
    }

    var left = anchor + leftEdge - halfWidth
    var right = anchor + rightEdge + halfWidth
    when {
        left < 0f -> { right -= left; left = 0f }
        right > viewportWidth -> { left -= right - viewportWidth; right = viewportWidth }
    }
    drawShape((left + right) / 2f, centerY, active.copy(width = right - left))
}

private fun DrawScope.drawShape(centerX: Float, centerY: Float, shape: ShapeParams) {
    val shrink = shape.strokeWidth.coerceAtLeast(0f) / 2f
    val topLeft = Offset(
        x = centerX - shape.width / 2f + shrink,
        y = centerY - shape.height / 2f + shrink,
    )
    val rectSize = Size(
        width = (shape.width - 2 * shrink).coerceAtLeast(0f),
        height = (shape.height - 2 * shrink).coerceAtLeast(0f),
    )
    val radius = CornerRadius(shape.cornerRadius, shape.cornerRadius)

    drawRoundRect(color = shape.color, topLeft = topLeft, size = rectSize, cornerRadius = radius)
    if (shape.strokeWidth > 0f && shape.strokeColor != Color.Transparent) {
        drawRoundRect(
            color = shape.strokeColor,
            topLeft = topLeft,
            size = rectSize,
            cornerRadius = radius,
            style = Stroke(width = shape.strokeWidth),
        )
    }
}

private const val MAX_OFFSET = 0.999999f
