package com.yandex.div.compose.font

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.AndroidFont
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.font.createFontFamilyResolver
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

@OptIn(ExperimentalTextApi::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class AndroidDivFontFamilyFactoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val factory = AndroidDivFontFamilyFactory(context.assets, TestReporter())
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
    @Config(sdk = [27])
    fun `Typeface source keeps the original typeface before API 28`() {
        val typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)

        val family = factory.create(
            DivFontSource.Typeface(typeface),
            FontWeight.Bold,
            variations = null,
        )

        assertSame(typeface, createFontFamilyResolver(context).resolve(family).value)
    }
}
