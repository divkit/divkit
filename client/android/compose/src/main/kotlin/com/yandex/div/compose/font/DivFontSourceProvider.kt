package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Provides a [DivFontSource] for the text elements inside `DivView`.
 *
 * The Compose layer takes the source returned by this provider, applies the
 * requested [FontWeight] and any `font_variation_settings` from the layout, and
 * wraps the result into an [androidx.compose.ui.text.font.FontFamily].
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface DivFontSourceProvider {
    fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource
}