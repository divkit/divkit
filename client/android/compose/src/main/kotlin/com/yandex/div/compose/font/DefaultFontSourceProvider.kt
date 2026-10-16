package com.yandex.div.compose.font

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontWeight

internal object DefaultFontSourceProvider : DivFontSourceProvider {
    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        return DivFontSource.Typeface(Typeface.DEFAULT)
    }
}
