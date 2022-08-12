package com.yandex.div.core.widget.slider

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Px

data class SliderTextStyle(
    @Px val fontSize: Float,
    val fontWeight: Typeface,
    @Px val offsetX: Float,
    @Px val offsetY: Float,
    @ColorInt val textColor: Int,
)
