package com.yandex.div.test.crossplatform

import android.graphics.PointF
import android.graphics.RectF

fun clipSwipeToBounds(from: PointF, to: PointF, bounds: RectF) {
    check(!bounds.isEmpty) { "Swipe target has no visible bounds" }

    val deltaX = to.x - from.x
    val deltaY = to.y - from.y
    val right = maxOf(bounds.left, bounds.right - 1f)
    val bottom = maxOf(bounds.top, bounds.bottom - 1f)
    val horizontalRange = clipAxis(from.x, deltaX, bounds.left, right)
    val verticalRange = clipAxis(from.y, deltaY, bounds.top, bottom)
    val start = maxOf(0f, horizontalRange.start, verticalRange.start)
    val end = minOf(1f, horizontalRange.endInclusive, verticalRange.endInclusive)
    check(start < end && (deltaX != 0f || deltaY != 0f)) {
        "Swipe has no visible segment: from=$from, to=$to, bounds=$bounds"
    }

    to.set(
        (from.x + deltaX * end).coerceIn(bounds.left, right),
        (from.y + deltaY * end).coerceIn(bounds.top, bottom)
    )
    from.set(
        (from.x + deltaX * start).coerceIn(bounds.left, right),
        (from.y + deltaY * start).coerceIn(bounds.top, bottom)
    )
}

private fun clipAxis(
    origin: Float,
    delta: Float,
    min: Float,
    max: Float
): ClosedFloatingPointRange<Float> {
    if (delta == 0f) {
        check(origin in min..max) { "Swipe does not intersect the visible bounds: $min..$max" }
        return 0f..1f
    }
    val first = (min - origin) / delta
    val last = (max - origin) / delta
    return minOf(first, last)..maxOf(first, last)
}
