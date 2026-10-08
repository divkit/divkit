package com.yandex.div.core.tooltip

import android.graphics.Point
import android.graphics.Rect
import android.view.View
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div2.DivTooltip

internal fun calculateTooltipPosition(
    data: TooltipData,
    tooltipSize: Point,
): Point {
    val anchorPosition = data.anchor.getPositionInWindow()
    val position = data.divTooltip.position.evaluate(data.anchorResolver)

    anchorPosition.x += position.horizontalOffset(
        anchorWidth = data.anchor.width,
        tooltipWidth = tooltipSize.x,
    )
    anchorPosition.y += position.verticalOffset(
        anchorHeight = data.anchor.height,
        tooltipHeight = tooltipSize.y,
    )

    val displayMetrics = data.anchor.resources.displayMetrics
    anchorPosition.x += data.divTooltip.offset?.x?.toPx(displayMetrics, data.anchorResolver) ?: 0
    anchorPosition.y += data.divTooltip.offset?.y?.toPx(displayMetrics, data.anchorResolver) ?: 0
    return anchorPosition
}

internal fun View.getPositionInWindow(): Point {
    val location = IntArray(2)
    getLocationInWindow(location)
    return Point(location[0], location[1])
}

internal fun View.getWindowFrame(): Rect {
    return Rect().also(::getWindowVisibleDisplayFrame)
}

internal fun View.getWindowContentSize(): Point {
    val windowFrame = getWindowFrame()
    if (windowFrame.width() > 0 && windowFrame.height() > 0) {
        return Point(windowFrame.width(), windowFrame.height())
    }

    return resources.displayMetrics.let { Point(it.widthPixels, it.heightPixels) }
}

private fun DivTooltip.Position.horizontalOffset(
    anchorWidth: Int,
    tooltipWidth: Int,
): Int = when (this) {
    DivTooltip.Position.LEFT,
    DivTooltip.Position.TOP_LEFT,
    DivTooltip.Position.BOTTOM_LEFT,
    -> -tooltipWidth

    DivTooltip.Position.TOP_RIGHT,
    DivTooltip.Position.RIGHT,
    DivTooltip.Position.BOTTOM_RIGHT,
    -> anchorWidth

    DivTooltip.Position.TOP,
    DivTooltip.Position.BOTTOM,
    DivTooltip.Position.CENTER,
    -> (anchorWidth - tooltipWidth) / 2
}

private fun DivTooltip.Position.verticalOffset(
    anchorHeight: Int,
    tooltipHeight: Int,
): Int = when (this) {
    DivTooltip.Position.TOP_LEFT,
    DivTooltip.Position.TOP,
    DivTooltip.Position.TOP_RIGHT,
    -> -tooltipHeight

    DivTooltip.Position.BOTTOM_LEFT,
    DivTooltip.Position.BOTTOM,
    DivTooltip.Position.BOTTOM_RIGHT,
    -> anchorHeight

    DivTooltip.Position.LEFT,
    DivTooltip.Position.RIGHT,
    DivTooltip.Position.CENTER,
    -> (anchorHeight - tooltipHeight) / 2
}
