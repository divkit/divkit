package com.yandex.div.evaluable.function

import kotlin.math.PI

private const val RADIANS_TO_DEGREES = 180.0 / PI

private const val DEGREES_TO_RADIANS = PI / 180.0

internal fun radiansToDegrees(value: Double): Double = value * RADIANS_TO_DEGREES

internal fun degreesToRadians(value: Double): Double = value * DEGREES_TO_RADIANS
