package com.yandex.div.compose

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Provides [FontFamily] for the text elements inside [DivView].
 *
 * @see DivComposeConfiguration
 */
@ExperimentalApi
interface DivFontFamilyProvider {
    fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily
}
