package com.yandex.div.render.common.pager

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PagerItemWindowTest {

    private val finiteWindow = PagerItemWindow(realItemCount = 5)
    private val edgeWindow = PagerItemWindow(realItemCount = 5, edgeItemCount = 2)
    private val unboundedWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
    private val singleItemWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 1)

    @Test
    fun `finite window contains only real items`() {
        assertEquals(5, finiteWindow.itemCount)
    }

    @Test
    fun `finite window keeps raw indices`() {
        assertEquals(3, finiteWindow.rawIndex(3))
    }

    @Test
    fun `finite window keeps real indices`() {
        assertEquals(3, finiteWindow.realIndex(3))
    }

    @Test
    fun `edge window adds requested copies on each side`() {
        assertEquals(9, edgeWindow.itemCount)
    }

    @Test
    fun `edge window places first real item after leading copies`() {
        assertEquals(2, edgeWindow.rawIndex(0))
    }

    @Test
    fun `edge window places last real item before trailing copies`() {
        assertEquals(6, edgeWindow.rawIndex(4))
    }

    @Test
    fun `edge window maps copies to real items`() {
        val realIndices = (0 until edgeWindow.itemCount).map(edgeWindow::realIndex)

        assertEquals(listOf(3, 4, 0, 1, 2, 3, 4, 0, 1), realIndices)
    }

    @Test
    fun `unbounded window places central cycle far from leading edge`() {
        val firstRealRawIndex = unboundedWindow.rawIndex(0)

        assertTrue(firstRealRawIndex > Int.MAX_VALUE / 4)
    }

    @Test
    fun `unbounded window wraps before central cycle to last real item`() {
        val firstRealRawIndex = unboundedWindow.rawIndex(0)

        assertEquals(4, unboundedWindow.realIndex(firstRealRawIndex - 1))
    }

    @Test
    fun `unbounded window wraps after central cycle to first real item`() {
        assertEquals(0, unboundedWindow.realIndex(unboundedWindow.rawIndex(4) + 1))
    }

    @Test
    fun `unbounded window places central cycle far from trailing edge`() {
        assertTrue(unboundedWindow.itemCount - unboundedWindow.rawIndex(4) > Int.MAX_VALUE / 4)
    }

    @Test
    fun `raw index normalizes real indices before shifting to the central cycle`() {
        assertEquals(listOf(6, 2, 6, 2, 3), listOf(-1, 0, 4, 5, 6).map(edgeWindow::rawIndex))
    }

    @Test
    fun `empty window has no items even when copies are requested`() {
        val window = PagerItemWindow(realItemCount = 0, edgeItemCount = 2)

        assertEquals(0, window.itemCount)
    }

    @Test
    fun `empty window cannot convert a raw index`() {
        val window = PagerItemWindow(realItemCount = 0)

        assertFailsWith<ArithmeticException> { window.realIndex(0) }
    }

    @Test
    fun `empty window cannot convert a real index`() {
        val window = PagerItemWindow(realItemCount = 0)

        assertFailsWith<ArithmeticException> { window.rawIndex(0) }
    }

    @Test
    fun `unbounded window aligns edge count to complete cycles`() {
        assertEquals(1_073_741_820, unboundedWindow.edgeItemCount)
    }

    @Test
    fun `unbounded window contains complete cycles`() {
        assertEquals(2_147_483_645, unboundedWindow.itemCount)
    }

    @Test
    fun `unbounded window starts with first real item`() {
        assertEquals(0, unboundedWindow.realIndex(0))
    }

    @Test
    fun `unbounded window ends with last real item`() {
        assertEquals(4, unboundedWindow.realIndex(unboundedWindow.itemCount - 1))
    }

    @Test
    fun `single item fills maximum unbounded window size`() {
        assertEquals(Int.MAX_VALUE, singleItemWindow.itemCount)
    }

    @Test
    fun `single item occupies first copy in unbounded window`() {
        assertEquals(0, singleItemWindow.realIndex(0))
    }

    @Test
    fun `single item occupies last copy in unbounded window`() {
        assertEquals(0, singleItemWindow.realIndex(singleItemWindow.itemCount - 1))
    }

    @Test
    fun `normalization wraps negative raw indices around the leading copies`() {
        val indices = listOf(-4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8)

        assertEquals(
            listOf(4, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 0, 1),
            indices.map { normalizePagerIndex(it, realItemCount = 5, edgeItemCount = 2) },
        )
    }

    @Test
    fun `normalization requires at least one real item`() {
        assertFailsWith<ArithmeticException> {
            normalizePagerIndex(rawIndex = 0, realItemCount = 0)
        }
    }
}
