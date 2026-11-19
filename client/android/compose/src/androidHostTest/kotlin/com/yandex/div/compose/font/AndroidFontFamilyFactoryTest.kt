package com.yandex.div.compose.font

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.AndroidFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.font.resolveAsTypeface
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.test.R
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class AndroidFontFamilyFactoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val reporter = TestReporter()
    private val factory = AndroidFontFamilyFactory(context.assets, reporter)

    private val variations = FontVariation.Settings(
        FontVariation.weight(650),
        FontVariation.width(125f),
    )

    @Test
    fun `resource source preserves the requested resource weight and variations`() {
        val family = factory.create(
            DivFontSource.Resource(R.font.roboto_flex),
            FontWeight.SemiBold,
            variations,
        )

        val font = assertIs<ResourceFont>(assertIs<FontListFontFamily>(family).single())
        assertEquals(R.font.roboto_flex, font.resId)
        assertEquals(FontWeight.SemiBold, font.weight)
        assertEquals(variations, font.variationSettings)
    }

    @Test
    fun `asset source loads the font with the requested weight and variations`() {
        val family = factory.create(
            DivFontSource.Asset("roboto_flex.ttf"),
            FontWeight.SemiBold,
            variations,
        )

        val font = assertIs<AndroidFont>(assertIs<FontListFontFamily>(family).single())
        assertEquals(FontWeight.SemiBold, font.weight)
        assertEquals(variations, font.variationSettings)
        assertNotNull(font.typefaceLoader.loadBlocking(context, font))
    }

    @Test
    fun `Typeface source is built with the requested bold weight`() {
        val fontFamily = factory.create(
            source = DivFontSource.Typeface(Typeface.DEFAULT),
            weight = FontWeight.Bold,
            variations = null,
        )

        assertEquals(FontWeight.Bold.weight, nativeTypeface(fontFamily).weight)
    }

    @Test
    fun `Typeface source is built with the requested light weight`() {
        val fontFamily = factory.create(
            source = DivFontSource.Typeface(Typeface.DEFAULT),
            weight = FontWeight.Light,
            variations = null,
        )

        assertEquals(FontWeight.Light.weight, nativeTypeface(fontFamily).weight)
    }

    @Test
    fun `italic Typeface source is built with the requested bold weight`() {
        val fontFamily = factory.create(
            source = DivFontSource.Typeface(
                Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            ),
            weight = FontWeight.Bold,
            variations = null,
        )

        assertEquals(FontWeight.Bold.weight, nativeTypeface(fontFamily).weight)
        assertTrue(nativeTypeface(fontFamily).isItalic)
    }

    @Test
    @Config(sdk = [27])
    fun `Typeface source keeps the original typeface before API 28`() {
        val typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)

        val family = factory.create(
            DivFontSource.Typeface(typeface),
            FontWeight.Bold,
            variations = null,
        )

        assertSame(typeface, nativeTypeface(family))
    }

    @Test
    fun `combining FontFamily source with variation settings reports a warning`() {
        factory.create(
            source = DivFontSource.FontFamily(FontFamily.Default),
            weight = FontWeight.Bold,
            variations = variations,
        )

        assertEquals(
            "font_variation_settings cannot be applied to a FontFamily. " +
                    "Use DivFontSource.Resource or DivFontSource.Asset for variable fonts.",
            reporter.lastWarning
        )
    }

    @Test
    fun `combining Typeface source with variation settings reports a warning`() {
        factory.create(
            source = DivFontSource.Typeface(Typeface.DEFAULT),
            weight = FontWeight.Bold,
            variations = variations,
        )

        assertEquals(
            "font_variation_settings cannot be applied to a Typeface. " +
                    "Use DivFontSource.Resource or DivFontSource.Asset for variable fonts.",
            reporter.lastWarning
        )
    }

    private fun nativeTypeface(fontFamily: FontFamily): Typeface {
        return createFontFamilyResolver(context).resolveAsTypeface(fontFamily).value
    }
}
