package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.views.container.SeparatorVisibility
import kotlin.test.Test
import kotlin.test.assertEquals

class WrapSeparatorLayoutTest {
    private val separator = SeparatorDrawInfo(
        visibility = SeparatorVisibility(showAtStart = true, showBetween = true, showAtEnd = true),
        color = Color.Black,
        shapeWidthDp = 12.dp,
        shapeHeightDp = 8.dp,
        cornerRadiusDp = 2.dp,
        marginStartDp = 1.dp,
        marginEndDp = 3.dp,
        marginTopDp = 2.dp,
        marginBottomDp = 1.dp,
        isCircle = false,
        stroke = null,
    )

    @Test
    fun betweenSeparatorsReplaceSpacingOnTheirAxis() {
        val horizontal = WrapSeparatorLayout(separator, separator, isHorizontal = true)
        val vertical = WrapSeparatorLayout(separator, separator, isHorizontal = false)

        assertEquals(16.dp, horizontal.itemSpacing { error("Item spacing is overridden") })
        assertEquals(11.dp, horizontal.lineSpacing { error("Line spacing is overridden") })
        assertEquals(11.dp, vertical.itemSpacing { error("Item spacing is overridden") })
        assertEquals(16.dp, vertical.lineSpacing { error("Line spacing is overridden") })
    }

    @Test
    fun edgeSeparatorsReserveSpaceAndKeepDefaultSpacing() {
        val start = separator.copy(
            visibility = SeparatorVisibility(showAtStart = true, showBetween = false, showAtEnd = false),
        )
        val end = separator.copy(
            visibility = SeparatorVisibility(showAtStart = false, showBetween = false, showAtEnd = true),
        )
        val layout = WrapSeparatorLayout(start, end, isHorizontal = true)

        assertEquals(5.dp, layout.itemSpacing { 5.dp })
        assertEquals(7.dp, layout.lineSpacing { 7.dp })
        assertEquals(16.dp, start.edgeSize(atStart = true, alongWidth = true))
        assertEquals(0.dp, start.edgeSize(atStart = false, alongWidth = true))
        assertEquals(0.dp, end.edgeSize(atStart = true, alongWidth = false))
        assertEquals(11.dp, end.edgeSize(atStart = false, alongWidth = false))
    }

    @Test
    fun rectangleStrokeRoundsAndClampsWhileCircleKeepsFractionalWidth() {
        val density = Density(1.5f)
        val stroke = BorderStroke(2.5.dp, Color.Red)
        val rectangle = separator.copy(stroke = stroke)
        val wideStrokeRectangle = separator.copy(stroke = BorderStroke(50.dp, Color.Red))
        val circle = separator.copy(shapeHeightDp = 12.dp, isCircle = true, stroke = stroke)

        assertEquals(4f, rectangle.toPx(density).strokeStyle?.width)
        assertEquals(6f, wideStrokeRectangle.toPx(density).strokeStyle?.width)
        assertEquals(3.75f, circle.toPx(density).strokeStyle?.width)
    }
}
