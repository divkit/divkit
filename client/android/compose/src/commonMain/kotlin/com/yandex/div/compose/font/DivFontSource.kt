package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily as ComposeFontFamily

/**
 * Platform-specific font source supplied by [DivFontSourceProvider].
 * The renderer uses it as an opaque descriptor for selection and caching.
 */
public expect sealed class DivFontSource {

    /**
     * Font is provided as a [androidx.compose.ui.text.font.FontFamily] instance.
     */
    public class FontFamily(fontFamily: ComposeFontFamily) : DivFontSource {
        public val fontFamily: ComposeFontFamily
    }
}
