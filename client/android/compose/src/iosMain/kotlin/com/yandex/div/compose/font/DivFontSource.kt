package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily as ComposeFontFamily

public actual sealed class DivFontSource {
    /** A font family provided by the iOS host. */
    public actual data class FontFamily actual constructor(
        public actual val fontFamily: ComposeFontFamily,
    ) : DivFontSource()
}
