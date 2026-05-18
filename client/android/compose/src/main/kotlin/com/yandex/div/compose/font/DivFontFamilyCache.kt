package com.yandex.div.compose.font

import android.content.res.AssetManager
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

/**
 * In-memory cache that deduplicates [FontFamily] instances built from the same
 * [DivFontSource] + [FontWeight] + [FontVariation.Settings] tuple.
 *
 * Scoped to a single `DivContext`. Accessed only during Compose composition, which is
 * confined to the main thread, so a plain [HashMap] is sufficient.
 */
@DivContextScope
internal class DivFontFamilyCache @Inject constructor(
    private val assets: AssetManager,
    private val reporter: DivReporter,
) {

    private val cache = HashMap<Key, FontFamily>()

    fun getOrCreate(
        source: DivFontSource,
        weight: FontWeight,
        variations: FontVariation.Settings?,
    ): FontFamily {
        val key = Key(source, weight, variations)
        return cache.getOrPut(key) { createFontFamily(key) }
    }

    @OptIn(ExperimentalTextApi::class)
    private fun createFontFamily(key: Key): FontFamily {
        val settings = key.variations ?: FontVariation.Settings()
        return when (val source = key.source) {
            is DivFontSource.Resource -> FontFamily(Font(source.resId, key.weight, variationSettings = settings))
            is DivFontSource.Asset -> FontFamily(Font(source.path, assets, key.weight, variationSettings = settings))
            is DivFontSource.Typeface -> {
                if (key.variations != null) {
                    reporter.reportWarning(
                        "font_variation_settings cannot be applied to a Typeface. " +
                                "Use DivFontSource.Resource or DivFontSource.Asset for variable fonts."
                    )
                }
                FontFamily(source.typeface)
            }
        }
    }

    private data class Key(
        val source: DivFontSource,
        val weight: FontWeight,
        val variations: FontVariation.Settings?,
    )
}
