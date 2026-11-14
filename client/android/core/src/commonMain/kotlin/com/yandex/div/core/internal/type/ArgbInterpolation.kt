@file:JvmName("ArgbInterpolation")

package com.yandex.div.core.internal.type

import com.yandex.div.core.annotations.InternalApi
import kotlin.jvm.JvmName
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Interpolates two ARGB colors the way `android.animation.ArgbEvaluator` does: the RGB channels
 * are linearised with gamma 2.2, interpolated and encoded back; alpha is interpolated linearly.
 */
@InternalApi
public fun interpolateArgb(fraction: Float, start: Int, end: Int): Int {
    val alpha = lerp(start.channel(ALPHA_SHIFT), end.channel(ALPHA_SHIFT), fraction) * MAX_CHANNEL
    val red = lerpGammaCorrected(start.channel(RED_SHIFT), end.channel(RED_SHIFT), fraction)
    val green = lerpGammaCorrected(start.channel(GREEN_SHIFT), end.channel(GREEN_SHIFT), fraction)
    val blue = lerpGammaCorrected(start.channel(BLUE_SHIFT), end.channel(BLUE_SHIFT), fraction)
    return (alpha.roundToInt() shl ALPHA_SHIFT) or
        (red.roundToInt() shl RED_SHIFT) or
        (green.roundToInt() shl GREEN_SHIFT) or
        blue.roundToInt()
}

private fun Int.channel(shift: Int): Float = ((this shr shift) and CHANNEL_MASK) / MAX_CHANNEL

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + fraction * (end - start)

private fun lerpGammaCorrected(start: Float, end: Float, fraction: Float): Float {
    val linear = lerp(start.toLinear(), end.toLinear(), fraction)
    return linear.toDouble().pow(1.0 / GAMMA).toFloat() * MAX_CHANNEL
}

private fun Float.toLinear(): Float = toDouble().pow(GAMMA).toFloat()

private const val GAMMA = 2.2
private const val MAX_CHANNEL = 255.0f
private const val CHANNEL_MASK = 0xff
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val BLUE_SHIFT = 0
