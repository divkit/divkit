package com.yandex.div.test.crossplatform

import android.graphics.PointF
import android.graphics.RectF

/** Converts [point], given in fractions of the full target size, to target pixels. */
fun tapPointWithinBounds(point: PointF, width: Float, height: Float, visibleBounds: RectF): PointF {
    // The last pixel of the target is size - 1, so x = 1 and y = 1 still hit it.
    val tapPoint = PointF(
        (width * point.x).coerceAtMost(maxOf(0f, width - 1f)),
        (height * point.y).coerceAtMost(maxOf(0f, height - 1f))
    )
    check(visibleBounds.contains(tapPoint.x, tapPoint.y)) {
        "Tap point (${point.x}, ${point.y}) = (${tapPoint.x}, ${tapPoint.y}) px " +
            "is outside the visible bounds $visibleBounds of the target"
    }
    return tapPoint
}
