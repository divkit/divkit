package com.yandex.div.core.view2

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DivTextLineHeightTest {
    private val context = Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock()).build()
    )

    @Test
    fun `standalone line height sets the height of a single line`() {
        val textView = renderText(text = "text", lineHeight = 40)

        assertEquals(listOf(0, 40), lineTops(textView))
        assertEquals(40, textView.height)
        assertLineHeightSpan(textView, start = 0, end = 4)
    }

    @Test
    fun `standalone line height sets the height of every paragraph`() {
        val textView = renderText(text = "first\nsecond\nthird", lineHeight = 40)

        assertEquals(listOf(0, 40, 80, 120), lineTops(textView))
        assertEquals(120, textView.height)
    }

    @Test
    fun `empty ranges preserve standalone line height`() {
        val plainText = renderText(text = "first\nsecond\nthird", lineHeight = 40, ranges = null)

        val richText = renderText(text = "first\nsecond\nthird", lineHeight = 40, ranges = emptyList())

        assertEquals(listOf(0, 40, 80, 120), lineTops(richText))
        assertEquals(plainText.height, richText.height)
        assertLineHeightSpan(richText, start = 0, end = 18)
    }

    @Test
    fun `empty text keeps a zero width space and the empty source span`() {
        val textView = renderText(text = "", lineHeight = 40)

        assertEquals(ZERO_WIDTH_SPACE, textView.text.toString())
        assertLineHeightSpan(textView, start = 0, end = 0)
        assertEquals(40, textView.height)
    }

    @Test
    fun `absent line height leaves the text without line height spans`() {
        val textView = renderText(text = "first\nsecond", lineHeight = null)

        assertEquals("first\nsecond", textView.text.toString())
        assertEquals(emptyList<LineHeightSpan>(), lineHeightSpans(textView).toList())
    }

    @Test
    fun `range line height overrides the standalone height for its paragraph`() {
        // Arrange
        val ranges = listOf(textRange(start = 2, end = 4, fontSizeUnit = DivSizeUnit.PX, lineHeight = 60))

        // Act
        val textView = renderText(text = "a\nb\nc", lineHeight = 40, ranges = ranges)

        // Assert
        assertEquals(listOf(0, 40, 100, 140), lineTops(textView))
        assertEquals(140, textView.height)
    }

    private fun renderText(
        text: String,
        lineHeight: Long?,
        ranges: List<DivText.Range>? = null,
    ): TextView {
        val divText = text(
            id = "text",
            text = constant(text),
            fontSize = 20,
            fontSizeUnit = DivSizeUnit.PX,
            lineHeight = lineHeight,
            ranges = ranges,
        )
        val divView = Div2View(context)
        divView.setData(data(divText), DivDataTag("line-height"))
        val textView = divView.findViewWithTag<TextView>("text")
        textView.typeface = Typeface.MONOSPACE
        divView.measure(makeExactSpec(320), makeUnspecifiedSpec())
        divView.layout(0, 0, divView.measuredWidth, divView.measuredHeight)
        return textView
    }

    private fun lineTops(textView: TextView): List<Int> {
        val layout = textView.layout
        return (0..layout.lineCount).map { layout.getLineTop(it) }
    }

    private fun lineHeightSpans(textView: TextView): Array<LineHeightSpan> {
        val text = textView.text as Spanned
        return text.getSpans(0, text.length, LineHeightSpan::class.java)
    }

    private fun assertLineHeightSpan(textView: TextView, start: Int, end: Int) {
        val text = textView.text as Spanned
        val span = lineHeightSpans(textView).single()
        assertEquals(start..end, text.getSpanStart(span)..text.getSpanEnd(span))
        assertEquals(Spanned.SPAN_INCLUSIVE_INCLUSIVE, text.getSpanFlags(span))
    }
}

private const val ZERO_WIDTH_SPACE = "\u200B"
