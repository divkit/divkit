package com.yandex.div.compose.pager

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PagerItemWindowTest {

    @Test
    fun `finite window keeps real indices`() {
        val window = PagerItemWindow(realItemCount = 5)

        assertEquals(5, window.itemCount)
        assertEquals(3, window.rawIndex(3))
        assertEquals(3, window.realIndex(3))
    }

    @Test
    fun `edge window adds requested copies on each side`() {
        val window = PagerItemWindow(realItemCount = 5, edgeItemCount = 2)

        assertEquals(9, window.itemCount)
        assertEquals(2, window.rawIndex(0))
        assertEquals(6, window.rawIndex(4))
    }

    @Test
    fun `edge window maps copies to real items`() {
        val window = PagerItemWindow(realItemCount = 5, edgeItemCount = 2)

        val realIndices = (0 until window.itemCount).map(window::realIndex)

        assertEquals(listOf(3, 4, 0, 1, 2, 3, 4, 0, 1), realIndices)
    }

    @Test
    fun `virtually unbounded window wraps around its centered real cycle`() {
        val window = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
        val firstRealRawIndex = window.rawIndex(0)

        assertTrue(firstRealRawIndex > Int.MAX_VALUE / 4)
        assertEquals(4, window.realIndex(firstRealRawIndex - 1))
        assertEquals(0, window.realIndex(window.rawIndex(4) + 1))
        assertTrue(window.itemCount - window.rawIndex(4) > Int.MAX_VALUE / 4)
    }
}
