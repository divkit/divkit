package com.yandex.div.compose.utils

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import com.yandex.div.internal.animation.springInterpolation
import com.yandex.div2.DivAnimationInterpolator

private val SpringEasing = Easing(::springInterpolation)

@Suppress("MagicNumber")
internal fun DivAnimationInterpolator.toEasing(): Easing = when (this) {
    DivAnimationInterpolator.LINEAR -> LinearEasing
    DivAnimationInterpolator.EASE -> CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    DivAnimationInterpolator.EASE_IN -> CubicBezierEasing(0.42f, 0f, 1f, 1f)
    DivAnimationInterpolator.EASE_OUT -> CubicBezierEasing(0f, 0f, 0.58f, 1f)
    DivAnimationInterpolator.EASE_IN_OUT -> CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    DivAnimationInterpolator.SPRING -> SpringEasing
}
