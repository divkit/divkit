package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class PagerScrollPositionTest {

    private val density = Density(1f)

    @Test
    fun `centered initial page uses a nearby first visible index`() {
        val position = calculateInitialScroll(
            defaultItem = 3,
            snapPosition = SnapPosition.Center,
            pageSize = 100.dp,
            itemSpacing = 0.dp,
            startPadding = 0.dp,
            endPadding = 0.dp,
            viewportSize = 300.dp,
            itemCount = 5,
            density = density,
        )

        assertEquals(InitialScroll(itemIndex = 2, scrollOffset = 0), position)
    }

    @Test
    fun `centered virtual page does not overflow absolute scroll arithmetic`() {
        val virtualPage = Int.MAX_VALUE / 2

        val position = calculateInitialScroll(
            defaultItem = virtualPage,
            snapPosition = SnapPosition.Center,
            pageSize = 100.dp,
            itemSpacing = 20.dp,
            startPadding = 0.dp,
            endPadding = 0.dp,
            viewportSize = 300.dp,
            itemCount = Int.MAX_VALUE,
            density = density,
        )

        assertEquals(InitialScroll(itemIndex = virtualPage - 1, scrollOffset = 20), position)
    }

    @Test
    fun `end aligned virtual page uses local position near target`() {
        val virtualPage = Int.MAX_VALUE / 2

        val position = calculateInitialScroll(
            defaultItem = virtualPage,
            snapPosition = SnapPosition.End,
            pageSize = 100.dp,
            itemSpacing = 0.dp,
            startPadding = 0.dp,
            endPadding = 0.dp,
            viewportSize = 300.dp,
            itemCount = Int.MAX_VALUE,
            density = density,
        )

        assertEquals(InitialScroll(itemIndex = virtualPage - 2, scrollOffset = 0), position)
    }
}
