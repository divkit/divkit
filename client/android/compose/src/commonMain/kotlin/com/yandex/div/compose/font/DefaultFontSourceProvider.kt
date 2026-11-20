package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

internal object DefaultFontSourceProvider : DivFontSourceProvider {
    private val defaultFontSource = DivFontSource.FontFamily(FontFamily.Default)

    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        return defaultFontSource
    }
}
