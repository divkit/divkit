package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.graphics.Colormap

internal data class AnimatedTextGradientData(
    val gradient: Gradient,
    val duration: Long,
) {
    sealed interface Gradient {
        val colormap: Colormap

        data class Linear(
            override val colormap: Colormap,
            val angle: Float,
        ) : Gradient

        data class Radial(
            override val colormap: Colormap,
            val radius: RadialGradientDrawable.Radius,
            val centerX: RadialGradientDrawable.Center,
            val centerY: RadialGradientDrawable.Center,
        ) : Gradient
    }
}
