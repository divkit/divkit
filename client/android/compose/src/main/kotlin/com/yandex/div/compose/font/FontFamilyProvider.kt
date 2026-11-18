package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class FontFamilyProvider @Inject constructor(
    private val sourceProvider: DivFontSourceProvider,
    private val cache: FontFamilyCache,
) {

    fun getFontFamily(
        name: String?,
        weight: FontWeight,
        variations: FontVariation.Settings?,
    ): FontFamily {
        val source = sourceProvider.getFontSource(name, weight)
        return cache.getOrCreate(source, weight, variations)
    }
}
