package com.yandex.div.compose

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.core.annotations.PublicApi

@PublicApi
interface DivFontFamilyProvider {
    fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily
}
