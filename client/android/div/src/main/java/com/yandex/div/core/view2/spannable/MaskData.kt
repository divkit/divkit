package com.yandex.div.core.view2.spannable

import androidx.annotation.ColorInt

internal sealed class MaskData {
    data class Particles(
        @ColorInt val color: Int,
        val density: Float,
        val isAnimated: Boolean,
        val isEnabled: Boolean,
        val particleSize: Float
    ) : MaskData()

    data class Solid(
        @ColorInt val color: Int,
        val isEnabled: Boolean
    ) : MaskData()
}
