package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight

internal fun interface DivFontFamilyFactory {
    fun create(
        source: DivFontSource,
        weight: FontWeight,
        variations: FontVariation.Settings?,
    ): FontFamily
}
