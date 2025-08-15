package com.yandex.div.internal.widget.slider

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Px

data class SliderTextStyle(
    @Px val fontSize: Float,
    val spacing: Float,
    val fontWeight: Typeface,
    @Px val offsetX: Float,
    @Px val offsetY: Float,
    @ColorInt val textColor: Int,
    val fontVariations: String?,
)
