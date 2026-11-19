package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

internal object DefaultFontSourceProvider : DivFontSourceProvider {
    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        return DivFontSource.FontFamily(FontFamily.Default)
    }
}
