package com.yandex.div.core.animation

import android.view.animation.Interpolator

class ReverseInterpolator(
    private val base: Interpolator
) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return 1.0f - base.getInterpolation(1.0f - input)
    }
}

fun Interpolator.reversed(): Interpolator {
    return ReverseInterpolator(this)
}
