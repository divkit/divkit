package com.yandex.div.core.animation

import android.view.animation.Interpolator

internal abstract class LookupTableInterpolator(
    private val values: FloatArray
) : Interpolator {

    override fun getInterpolation(input: Float) = lookupTableInterpolation(input, values)
}
