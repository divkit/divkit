package com.yandex.div.core.animation

import android.view.animation.Interpolator
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div2.DivAnimationInterpolator

internal val defaultInterpolator: Interpolator =
    DivAnimationInterpolator.EASE_IN_OUT.androidInterpolator

internal fun Interpolator.reversed(): Interpolator {
    return Interpolator { input -> getInterpolation(1.0f - input) }
}
