package com.yandex.div.compose.font

import androidx.annotation.FontRes
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Describes the source of a font supplied by [DivFontSourceProvider]. The Compose
 * rendering layer constructs the actual [androidx.compose.ui.text.font.Font] from
 * this descriptor and applies weight / variation settings on its side.
 */
@ExperimentalApi
sealed class DivFontSource {

    /** Font is stored as an Android font resource. Supports variation settings. */
    data class Resource(@param:FontRes val resId: Int) : DivFontSource()

    /** Font is stored as an app asset. Supports variation settings. */
    data class Asset(val path: String) : DivFontSource()

    /**
     * Font is provided as an already built [android.graphics.Typeface]. Variation
     * settings cannot be applied to a pre-built typeface — use [Resource] or [Asset]
     * for variable fonts that need runtime variation support.
     */
    data class Typeface(val typeface: android.graphics.Typeface) : DivFontSource()
}
