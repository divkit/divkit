package com.yandex.div.core.animation

import android.view.animation.Interpolator
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.animation.springInterpolation

@OptIn(InternalApi::class)
internal class SpringInterpolator : Interpolator {
    override fun getInterpolation(input: Float) = springInterpolation(input)
}
