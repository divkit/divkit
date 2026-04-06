package com.yandex.divkit.demo.font

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.DivFontFamilyProvider
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider

class ComposeFontFamilyProvider(context: Context) : DivFontFamilyProvider {

    private val defaultProvider = YandexSansDivTypefaceProvider(context)
    private val additionalProviders = mapOf(
        "display" to YandexSansDisplayDivTypefaceProvider(context) as DivTypefaceProvider,
        "condensed" to YandexSansCondensedTypefaceProvider(context) as DivTypefaceProvider,
        "roboto_flex" to RobotoFlexTypefaceProvider(context) as DivTypefaceProvider,
    )

    override fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily {
        val provider = fontFamilyName?.let { additionalProviders[it] } ?: defaultProvider
        val typeface = provider.getTypefaceFor(weight.weight) ?: return FontFamily.Default
        return FontFamily(typeface)
    }
}
