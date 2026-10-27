package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@InternalApi
public object AnimatedTextGradientMath {

    @PublishedApi
    internal const val DEGREES_TO_RADIANS: Double = PI / 180.0

    @JvmStatic
    public fun phase(frameTimeMillis: Long, durationMillis: Long): Float {
        val safeDuration = durationMillis.coerceAtLeast(1L)
        return (frameTimeMillis % safeDuration).toFloat() / safeDuration
    }

    /**
     * Passes the linear gradient translation to [consumer], so that callers updating it on every frame
     * don't allocate.
     */
    public inline fun <R> linearTranslation(
        angleDegrees: Float,
        width: Float,
        height: Float,
        phase: Float,
        consumer: (x: Float, y: Float) -> R,
    ): R {
        val angleRadians = angleDegrees * DEGREES_TO_RADIANS
        val xDirection = cos(angleRadians).toFloat()
        val yDirection = -sin(angleRadians).toFloat()
        val distance = abs(width * xDirection) + abs(height * yDirection)
        val offset = symmetricOffset(distance, phase)
        return consumer(xDirection * offset, yDirection * offset)
    }

    @JvmStatic
    public fun radialTranslation(width: Float, phase: Float): Float {
        return symmetricOffset(width, phase)
    }

    @PublishedApi
    internal fun symmetricOffset(distance: Float, phase: Float): Float {
        return -distance + 2f * distance * phase.coerceIn(0f, 1f)
    }
}
