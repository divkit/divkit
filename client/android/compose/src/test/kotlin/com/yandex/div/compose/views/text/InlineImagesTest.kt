package com.yandex.div.compose.views.text

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class InlineImagesTest {

    @Test
    fun `images are inserted at original text offsets`() {
        val builder = AnnotatedString.Builder()

        val offsets = builder.appendTextWithInlineImages(
            text = "abc",
            images = listOf(
                inlineImage(id = "first", position = 0),
                inlineImage(id = "second", position = 2),
            ),
        )

        assertEquals("\u2060ab\u2060\u2060c", builder.toAnnotatedString().text)
        assertEquals(1, offsets.rangeStart(0))
        assertEquals(3, offsets.rangeEnd(2))
    }

    @Test
    fun `images at consecutive offsets share a word boundary`() {
        val builder = AnnotatedString.Builder()

        builder.appendTextWithInlineImages(
            text = "abc",
            images = listOf(
                inlineImage(id = "first", position = 1),
                inlineImage(id = "second", position = 2),
            ),
        )

        assertEquals("a\u2060\u2060b\u2060c", builder.toAnnotatedString().text)
    }

    private fun inlineImage(
        id: String,
        position: Int,
    ) = InlineImageData(
        id = id,
        position = position,
        url = mock(),
        width = 1.sp,
        height = 1.sp,
        contentWidth = 1.dp,
        contentHeight = 1.dp,
        contentAlignment = Alignment.CenterStart,
        verticalAlignment = PlaceholderVerticalAlign.Center,
        baselineOffset = 0.dp,
        descentPlaceholderHeight = null,
        colorFilter = null,
        accessibilityDescription = null,
        accessibilityRole = null,
    )
}
