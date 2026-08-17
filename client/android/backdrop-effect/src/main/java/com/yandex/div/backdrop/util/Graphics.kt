package com.yandex.div.backdrop.util

import kotlin.math.ceil

internal fun alphaByte(value: Float): Int {
    return ceil(value * 255).toInt().coerceIn(0, 255)
}

internal fun multiplyAlphaBytes(leftAlpha: Int, rightAlpha: Int): Int {
    return (leftAlpha * rightAlpha / 255.0f).toInt().coerceIn(0, 255)
}
