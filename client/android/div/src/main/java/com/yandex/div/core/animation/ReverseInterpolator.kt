package com.yandex.div.core.animation

import android.view.animation.Interpolator

internal class ReverseInterpolator(
    private val base: Interpolator
) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return 1.0f - base.getInterpolation(1.0f - input)
    }
}

internal fun Interpolator.reversed(): Interpolator {
    return ReverseInterpolator(this)
}
