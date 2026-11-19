package com.yandex.div.compose.font

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class FontFamilyCacheTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val source = DivFontSource.Typeface(Typeface.DEFAULT)
    private val factory = AndroidFontFamilyFactory(context.assets, TestReporter())
    private val cache = FontFamilyCache(factory)

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
    fun `each FontFamilyCache instance has its own storage`() {
        val otherCache = FontFamilyCache(factory)

        val fromCache = cache.getOrCreate(source, FontWeight.Normal, null)
        val fromOtherCache = otherCache.getOrCreate(source, FontWeight.Normal, null)

        assertNotSame(fromCache, fromOtherCache)
    }
}
