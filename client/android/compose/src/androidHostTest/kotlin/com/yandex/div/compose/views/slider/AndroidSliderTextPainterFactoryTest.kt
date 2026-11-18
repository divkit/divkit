package com.yandex.div.compose.views.slider

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class AndroidSliderTextPainterFactoryTest {

    @get:Rule
    val rule = createComposeRule()

    private val factory = AndroidSliderTextPainterFactory()
    private val resolver = fontFamilyResolver()
    private val currentResolver = mutableStateOf(resolver)
    private var painter: SliderTextPainter? = null
    private val style = mutableStateOf(
        SliderTextStyle(
            fontSize = 16f,
            letterSpacing = 0f,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            offsetX = 0f,
            offsetY = 0f,
            color = 0xFF000000.toInt(),
        )
    )

    @Test
    fun `font family change resolves the new family`() {
        setContent()

        style.value = style.value.copy(fontFamily = FontFamily.Monospace)
        rule.waitForIdle()

        verify(resolver).resolve(
            FontFamily.Monospace, FontWeight.Normal, FontStyle.Normal, FontSynthesis.All
        )
    }

    @Test
    fun `font weight change resolves the new weight`() {
        setContent()

        style.value = style.value.copy(fontWeight = FontWeight.Bold)
        rule.waitForIdle()

        verify(resolver).resolve(
            FontFamily.SansSerif, FontWeight.Bold, FontStyle.Normal, FontSynthesis.All
        )
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `resolver change resolves the font through the new resolver`() {
        setContent()
        val replacement = fontFamilyResolver(Typeface.MONOSPACE)

        currentResolver.value = replacement
        rule.waitForIdle()
        val nativeCanvas: android.graphics.Canvas = mock()
        CanvasDrawScope().draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(nativeCanvas),
            size = Size(100f, 100f),
        ) {
            assertNotNull(painter).draw(this, "10", centerX = 50f)
        }

        val paint = argumentCaptor<Paint>()
        verify(nativeCanvas).drawText(eq("10"), any(), any(), paint.capture())
        assertSame(Typeface.MONOSPACE, paint.firstValue.typeface)
        verify(replacement).resolve(
            FontFamily.SansSerif, FontWeight.Normal, FontStyle.Normal, FontSynthesis.All
        )
    }

    @Test
    fun `color change does not resolve the font again`() {
        setContent()

        style.value = style.value.copy(color = 0xFFFF0000.toInt())
        rule.waitForIdle()

        verify(resolver).resolve(
            FontFamily.SansSerif, FontWeight.Normal, FontStyle.Normal, FontSynthesis.All
        )
        verifyNoMoreInteractions(resolver)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(LocalFontFamilyResolver provides currentResolver.value) {
                painter = factory.rememberPainter(style.value)
            }
        }
    }

    private fun fontFamilyResolver(
        typeface: Typeface = Typeface.DEFAULT,
    ): FontFamily.Resolver = mock {
        on { resolve(anyOrNull(), any(), any(), any()) } doReturn mutableStateOf<Any>(typeface)
    }
}
