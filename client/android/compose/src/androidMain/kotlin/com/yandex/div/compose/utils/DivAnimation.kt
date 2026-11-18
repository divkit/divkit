package com.yandex.div.compose.utils

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivAnimationInterpolator

@Composable
internal fun <T> DivAnimation.observedAnimationSpec(): TweenSpec<T> {
    return tween(
        durationMillis = duration.observedIntValue(),
        delayMillis = startDelay.observedIntValue(),
        easing = interpolator.observedValue().toEasing(),
    )
}

internal fun DivAnimationInterpolator.toEasing(): Easing {
    return when (this) {
        DivAnimationInterpolator.LINEAR -> LinearEasing
        DivAnimationInterpolator.EASE -> Ease
        DivAnimationInterpolator.EASE_IN -> EaseIn
        DivAnimationInterpolator.EASE_OUT -> EaseOut
        DivAnimationInterpolator.EASE_IN_OUT -> EaseInOut
        DivAnimationInterpolator.SPRING -> Ease
    }
}
