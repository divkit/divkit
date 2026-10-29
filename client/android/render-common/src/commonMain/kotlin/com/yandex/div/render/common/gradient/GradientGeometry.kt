package com.yandex.div.render.common.gradient

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@InternalApi
public object GradientGeometry {

    private const val SNAP_EPSILON = 0.0001f

    public enum class RelativeRadius {
        NEAREST_CORNER, FARTHEST_CORNER, NEAREST_SIDE, FARTHEST_SIDE
    }

    /**
     * Delivers endpoints directly to the renderer without allocating a coordinate holder.
     */
    public inline fun <R> linearEndpoints(
        angleDegrees: Double,
        width: Float,
        height: Float,
        consumer: (startX: Float, startY: Float, endX: Float, endY: Float) -> R,
    ): R {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val angleRadians = (angleDegrees * PI / 180f).toFloat()
        val gradientLength = abs(width * cos(angleRadians)) + abs(height * sin(angleRadians))
        val widthDelta = (cos(angleRadians) * gradientLength / 2f).snapToZero()
        val heightDelta = (sin(angleRadians) * gradientLength / 2f).snapToZero()
        return consumer(
            halfWidth - widthDelta,
            halfHeight + heightDelta,
            halfWidth + widthDelta,
            halfHeight - heightDelta,
        )
    }

    public fun absoluteCenter(value: Float, isRelative: Boolean, parentSize: Float): Float =
        if (isRelative) value * parentSize else value

    public fun relativeRadius(
        type: RelativeRadius,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
    ): Float {
        val left = abs(centerX - 0f)
        val right = abs(centerX - width)
        val top = abs(centerY - 0f)
        val bottom = abs(centerY - height)
        return when (type) {
            RelativeRadius.NEAREST_CORNER -> minOf(
                minOf(distance(left, top), distance(right, top)),
                minOf(distance(right, bottom), distance(left, bottom)),
            )
            RelativeRadius.FARTHEST_CORNER -> maxOf(
                maxOf(distance(left, top), distance(right, top)),
                maxOf(distance(right, bottom), distance(left, bottom)),
            )
            RelativeRadius.NEAREST_SIDE -> minOf(minOf(left, right), minOf(top, bottom))
            RelativeRadius.FARTHEST_SIDE -> maxOf(maxOf(left, right), maxOf(top, bottom))
        }
    }

    private fun distance(x: Float, y: Float): Float = sqrt(x.pow(2) + y.pow(2))

    @PublishedApi
    internal fun Float.snapToZero(): Float = if (abs(this) <= SNAP_EPSILON) 0f else this
}
