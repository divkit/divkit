package com.yandex.div.compose.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

/**
 * In-memory cache that deduplicates [FontFamily] instances built from the same
 * [DivFontSource] + [FontWeight] + [FontVariation.Settings] tuple.
 *
 * Scoped to a single `DivContext`. Accessed only during Compose composition, which is
 * confined to the main thread, so a plain [HashMap] is sufficient.
 */
@DivContextScope
internal class DivFontFamilyCache @Inject constructor(
    private val factory: DivFontFamilyFactory,
) {

    private val cache = HashMap<Key, FontFamily>()

    fun getOrCreate(
        source: DivFontSource,
        weight: FontWeight,
        variations: FontVariation.Settings?,
    ): FontFamily {
        val key = Key(source, weight, variations)
        return cache.getOrPut(key) { factory.create(source, weight, variations) }
    }

    private data class Key(
        val source: DivFontSource,
        val weight: FontWeight,
        val variations: FontVariation.Settings?,
    )
}
