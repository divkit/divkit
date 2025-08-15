package com.yandex.div.core.view2.spannable

import androidx.annotation.ColorInt
import androidx.annotation.Px

internal data class ShadowData(
    @Px val offsetX: Float,
    @Px val offsetY: Float,
    @Px val radius: Float,
    @ColorInt val color: Int
)
