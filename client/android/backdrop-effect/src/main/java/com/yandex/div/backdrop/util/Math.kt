package com.yandex.div.backdrop.util

import kotlin.math.PI

internal fun Float.normalizeDegrees(): Float {
    return (this % 360.0f + 360.0f) % 360.0f
}

internal fun Float.toRadians(): Float {
    return this * (PI.toFloat() / 180.0f)
}
