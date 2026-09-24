package com.yandex.div.compose.font

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class LineMetricsCacheTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val fontFamilyResolver = createFontFamilyResolver(context)
    private val density = Density(density = 2f, fontScale = 1f)
    private val cache = LineMetricsCache()

    private val defaultFontFamily = FontFamily(Typeface.DEFAULT)
    private val monospaceFontFamily = FontFamily(Typeface.MONOSPACE)

    private fun getOrCreate(
        style: TextStyle = textStyle(),
        density: Density = this.density,
        resolver: FontFamily.Resolver = fontFamilyResolver,
    ) = cache.getOrCreate(resolver, density, style)

    @Test
    fun `metrics are measured from the font`() {
        val metrics = getOrCreate()

        assertEquals(LineMetrics(ascent = 15.sp, descent = 4.sp, heightPx = 38f), metrics)
    }

    @Test
    fun `styles that differ only visually share a single entry`() {
        val first = getOrCreate(textStyle(color = Color.Red, letterSpacing = 0.1f))
        val second = getOrCreate(textStyle(color = Color.Blue, letterSpacing = 0.5f))

        assertEquals(first, second)
        assertEquals(1, cache.size)
    }

    @Test
    fun `line height does not affect the key`() {
        getOrCreate(textStyle())
        getOrCreate(
            textStyle().copy(
                lineHeight = 4.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both,
                    mode = LineHeightStyle.Mode.Tight,
                ),
            )
        )

        assertEquals(1, cache.size)
    }

    @Test
    fun `cache stores separate entries for different font sizes`() {
        val small = getOrCreate(textStyle(fontSize = 13f))
        val large = getOrCreate(textStyle(fontSize = 26f))

        assertNotEquals(small.heightPx, large.heightPx)
        assertEquals(2, cache.size)
    }

    @Test
    fun `cache stores separate entries for different font weights`() {
        getOrCreate(textStyle(fontWeight = FontWeight.Normal))
        getOrCreate(textStyle(fontWeight = FontWeight.Bold))

        assertEquals(2, cache.size)
    }

    @Test
    fun `cache stores separate entries for different font families`() {
        getOrCreate(textStyle(fontFamily = defaultFontFamily))
        getOrCreate(textStyle(fontFamily = monospaceFontFamily))

        assertEquals(2, cache.size)
    }

    @Test
    fun `changing density drops previously cached entries`() {
        getOrCreate()
        assertEquals(1, cache.size)

        getOrCreate(density = Density(density = 3f, fontScale = 1f))

        assertEquals(1, cache.size)
    }

    @Test
    fun `changing font scale drops previously cached entries`() {
        getOrCreate()
        assertEquals(1, cache.size)

        getOrCreate(density = Density(density = 2f, fontScale = 2f))

        assertEquals(1, cache.size)
    }

    @Test
    fun `changing font family resolver drops previously cached entries`() {
        getOrCreate()
        assertEquals(1, cache.size)

        getOrCreate(resolver = createFontFamilyResolver(context))

        assertEquals(1, cache.size)
    }

    @Test
    fun `each cache instance has its own storage`() {
        val otherCache = LineMetricsCache()

        getOrCreate()
        otherCache.getOrCreate(fontFamilyResolver, density, textStyle())

        assertEquals(1, cache.size)
        assertEquals(1, otherCache.size)
    }

    private fun textStyle(
        fontSize: Float = 16f,
        fontWeight: FontWeight = FontWeight.Normal,
        fontFamily: FontFamily = defaultFontFamily,
        color: Color = Color.Black,
        letterSpacing: Float = 0f,
    ) = TextStyle(
        color = color,
        fontSize = fontSize.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing.em,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )
}
