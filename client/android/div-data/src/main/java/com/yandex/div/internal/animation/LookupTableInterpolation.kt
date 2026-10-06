package com.yandex.div.internal.animation

import com.yandex.div.core.annotations.InternalApi

@InternalApi
fun lookupTableInterpolation(input: Float, values: FloatArray): Float {
    if (input <= 0f) return 0f
    if (input >= 1f) return 1f

    val stepSize = 1f / values.lastIndex
    val position = (input * values.lastIndex).toInt().coerceAtMost(values.size - 2)
    val quantized = position * stepSize
    val weight = (input - quantized) / stepSize

    return values[position] + weight * (values[position + 1] - values[position])
}
