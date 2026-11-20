package com.yandex.div.compose.font

import androidx.annotation.FontRes
import androidx.compose.ui.text.font.FontFamily as ComposeFontFamily

/**
 * Platform-specific font source supplied by [DivFontSourceProvider]. Shared font
 * selection and caching treat it as an opaque descriptor. The platform font factory
 * constructs the [androidx.compose.ui.text.font.Font] and applies weight / variation
 * settings.
 */
actual sealed class DivFontSource {

    /**
     * Font is provided as a [androidx.compose.ui.text.font.FontFamily] instance.
     */
    data class FontFamily(val fontFamily: ComposeFontFamily): DivFontSource()

    /**
     * Font is stored as an Android font resource. Supports variation settings.
     */
    data class Resource(@FontRes val resId: Int) : DivFontSource()

    /**
     * Font is stored as an app asset. Supports variation settings.
     */
    data class Asset(val path: String) : DivFontSource()

    /**
     * Font is provided as an already built [android.graphics.Typeface]. The requested
     * weight is applied with [android.graphics.Typeface.create] on API 28+, keeping the
     * italic style of the typeface. Variation settings cannot be applied to a pre-built
     * typeface — use [Resource] or [Asset] for variable fonts that need runtime
     * variation support.
     */
    @Deprecated("Use FontFamily instead")
    data class Typeface(val typeface: android.graphics.Typeface) : DivFontSource()
}
