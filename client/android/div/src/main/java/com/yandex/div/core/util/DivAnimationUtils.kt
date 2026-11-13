package com.yandex.div.core.util

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import com.yandex.div.core.animation.reversed
import com.yandex.div2.DivAnimationDirection
import com.yandex.div2.DivAnimationInterpolator

private val linearInterpolator = LinearInterpolator()
private val easeInterpolator = PathInterpolator(0.25f, 0.1f, 0.25f, 1f)
private val easeInInterpolator = PathInterpolator(0.42f, 0f, 1f, 1f)
private val easeOutInterpolator = PathInterpolator(0f, 0f, 0.58f, 1f)
private val easeInOutInterpolator = PathInterpolator(0.42f, 0f, 0.58f, 1f)

internal val DivAnimationInterpolator.androidInterpolator: Interpolator
    get() = when (this) {
        DivAnimationInterpolator.LINEAR -> linearInterpolator
        DivAnimationInterpolator.EASE -> easeInterpolator
        DivAnimationInterpolator.EASE_IN -> easeInInterpolator
        DivAnimationInterpolator.EASE_OUT -> easeOutInterpolator
        DivAnimationInterpolator.EASE_IN_OUT -> easeInOutInterpolator
        DivAnimationInterpolator.SPRING -> easeInterpolator
    }

internal fun DivAnimationInterpolator.androidInterpolator(reverse: Boolean): Interpolator {
    return if (reverse) {
        androidInterpolator.reversed()
    } else {
        androidInterpolator
    }
}

internal val DivAnimationDirection.isReversed: Boolean
    get() {
        return when (this) {
            DivAnimationDirection.REVERSE, DivAnimationDirection.ALTERNATE_REVERSE -> true
            else -> false
        }
    }

internal val DivAnimationDirection.isAlternated: Boolean
    get() {
        return when (this) {
            DivAnimationDirection.ALTERNATE, DivAnimationDirection.ALTERNATE_REVERSE -> true
            else -> false
        }
    }
