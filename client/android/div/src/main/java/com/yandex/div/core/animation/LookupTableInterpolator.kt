package com.yandex.div.core.animation

import android.view.animation.Interpolator
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.animation.lookupTableInterpolation

@OptIn(InternalApi::class)
internal abstract class LookupTableInterpolator(
    private val values: FloatArray
) : Interpolator {

    override fun getInterpolation(input: Float) = lookupTableInterpolation(input, values)
}
