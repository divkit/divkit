package com.yandex.div.core.util

import android.graphics.PointF
import com.yandex.div.core.annotations.InternalApi
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@InternalApi
public object AnimatedTextGradientMath {

    @JvmStatic
    public fun phase(frameTimeMillis: Long, durationMillis: Long): Float {
        val safeDuration = durationMillis.coerceAtLeast(1L)
        return (frameTimeMillis % safeDuration).toFloat() / safeDuration
    }

    @JvmStatic
    public fun linearTranslation(
        angleDegrees: Float,
        width: Float,
        height: Float,
        phase: Float,
        result: PointF = PointF(),
    ): PointF {
        val angleRadians = Math.toRadians(angleDegrees.toDouble())
        val xDirection = cos(angleRadians).toFloat()
        val yDirection = -sin(angleRadians).toFloat()
        val distance = abs(width * xDirection) + abs(height * yDirection)
        val offset = symmetricOffset(distance, phase)
        result.set(xDirection * offset, yDirection * offset)
        return result
    }

    @JvmStatic
    public fun radialTranslation(width: Float, phase: Float): Float {
        return symmetricOffset(width, phase)
    }

    private fun symmetricOffset(distance: Float, phase: Float): Float {
        return -distance + 2f * distance * phase.coerceIn(0f, 1f)
    }
}
