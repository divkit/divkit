package com.yandex.div.core.view2.spannable

import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.yandex.div2.DivSizeUnit

internal data class TextData(
    val text: String,
    @Px val fontSize: Int,
    val fontSizeValue: Int,
    val fontSizeUnit: DivSizeUnit,
    val fontFamily: String?,
    @Px val lineHeight: Int?,
    @ColorInt val textColor: Int
) {
    val textLength: Int = text.length
}
