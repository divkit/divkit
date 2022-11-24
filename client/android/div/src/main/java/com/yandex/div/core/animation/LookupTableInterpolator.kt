package com.yandex.div.core.animation

import android.view.animation.Interpolator

internal abstract class LookupTableInterpolator(
    private val values: FloatArray
) : Interpolator {

    private val stepSize: Float = 1.0f / values.lastIndex

    override fun getInterpolation(input: Float): Float {
        if (input <= 0.0f) return 0.0f
        if (input >= 1.0f) return 1.0f

        // Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when
        // we lerp (linearly interpolate) in the return statement
        val position = (input * values.lastIndex).toInt().coerceAtMost(values.size - 2)

        // Calculate values to account for small offsets as the lookup table has discrete values
        val quantized = position * stepSize
        val diff = input - quantized
        val weight = diff / stepSize

        // Linearly interpolate between the table values
        return values[position] + weight * (values[position + 1] - values[position])
    }
}
