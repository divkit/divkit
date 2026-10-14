package com.yandex.div.compose.utils

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import com.yandex.div2.DivAnimationInterpolator

internal fun DivAnimationInterpolator.toEasing(): Easing = when (this) {
    DivAnimationInterpolator.LINEAR -> LinearEasing
    DivAnimationInterpolator.EASE -> Ease
    DivAnimationInterpolator.EASE_IN -> EaseIn
    DivAnimationInterpolator.EASE_OUT -> EaseOut
    DivAnimationInterpolator.EASE_IN_OUT -> EaseInOut
    DivAnimationInterpolator.SPRING -> Ease
}
