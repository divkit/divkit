package com.yandex.div.compose.font

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivReporter
import java.util.Collections
import java.util.IdentityHashMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalTextApi::class)
@RunWith(AndroidJUnit4::class)
class DivFontFamilyCacheTest {

    private val assets: AssetManager =
        ApplicationProvider.getApplicationContext<Context>().assets
    private val reporter: DivReporter = mock()
    private val source = DivFontSource.Typeface(Typeface.DEFAULT)
    private val cache = DivFontFamilyCache(assets, reporter)

    @Test
    fun `cache returns same FontFamily instance for the same key`() {
        val first = cache.getOrCreate(source, FontWeight.Normal, null)
        val second = cache.getOrCreate(source, FontWeight.Normal, null)
        val third = cache.getOrCreate(source, FontWeight.Normal, null)

        assertSame(first, second)
        assertSame(first, third)
    }

    @Test
    fun `cache stores separate entries for different weights`() {
        val normal = cache.getOrCreate(source, FontWeight.Normal, null)
        val bold = cache.getOrCreate(source, FontWeight.Bold, null)
        val normalAgain = cache.getOrCreate(source, FontWeight.Normal, null)

        assertNotSame(normal, bold)
        assertSame(normal, normalAgain)
    }

    @Test
    fun `cache stores separate entries for different variation settings`() {
        val plain = cache.getOrCreate(source, FontWeight.Normal, null)
        val weighted = cache.getOrCreate(
            source,
            FontWeight.Normal,
            FontVariation.Settings(FontVariation.weight(500)),
        )

        assertNotSame(plain, weighted)
    }

    @Test
    fun `cache hits when variation settings are structurally equal`() {
        val first = cache.getOrCreate(
            source,
            FontWeight.Normal,
            FontVariation.Settings(FontVariation.weight(500)),
        )
        val second = cache.getOrCreate(
            source,
            FontWeight.Normal,
            FontVariation.Settings(FontVariation.weight(500)),
        )

        assertSame(first, second)
    }

    @Test
    fun `cache stores separate entries for different font sources`() {
        val defaultTypeface = DivFontSource.Typeface(Typeface.DEFAULT)
        val boldTypeface = DivFontSource.Typeface(Typeface.DEFAULT_BOLD)

        val a = cache.getOrCreate(defaultTypeface, FontWeight.Normal, null)
        val b = cache.getOrCreate(boldTypeface, FontWeight.Normal, null)

        assertNotSame(a, b)
    }

    @Test
    fun `cached path deduplicates repeated calls into a single FontFamily instance`() {
        val sampleSize = 100

        val withCache = identitySet<FontFamily>().apply {
            repeat(sampleSize) {
                add(cache.getOrCreate(source, FontWeight.Normal, null))
            }
        }

        assertEquals(
            "Repeated calls with the same key should collapse to a single shared FontFamily",
            1,
            withCache.size,
        )
    }

    @Test
    fun `each DivFontFamilyCache instance has its own storage`() {
        val other = DivFontFamilyCache(assets, reporter)

        val fromCache = cache.getOrCreate(source, FontWeight.Normal, null)
        val fromOther = other.getOrCreate(source, FontWeight.Normal, null)

        assertNotSame(
            "Each DivFontFamilyCache instance is scoped independently",
            fromCache,
            fromOther,
        )
    }

    @Test
    fun `combining Typeface source with variation settings reports a warning`() {
        cache.getOrCreate(
            source,
            FontWeight.Normal,
            FontVariation.Settings(FontVariation.weight(500)),
        )

        verify(reporter).reportWarning(any())
    }

    private fun <T> identitySet(): MutableSet<T> =
        Collections.newSetFromMap(IdentityHashMap())
}
