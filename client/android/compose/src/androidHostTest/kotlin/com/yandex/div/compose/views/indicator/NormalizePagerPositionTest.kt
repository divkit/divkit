package com.yandex.div.compose.views.indicator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NormalizePagerPositionTest {

    private companion object {
        const val EPSILON = 1e-5f
        const val ITEMS = 5
    }

    @Test
    fun `at rest on first page returns first page with zero offset`() {
        val (page, offset) = normalizePagerPosition(rawPage = 0, rawOffset = 0f, itemsCount = ITEMS)

        assertEquals(0, page)
        assertEquals(0f, offset, EPSILON)
    }

    @Test
    fun `at rest on middle page returns that page with zero offset`() {
        val (page, offset) = normalizePagerPosition(rawPage = 2, rawOffset = 0f, itemsCount = ITEMS)

        assertEquals(2, page)
        assertEquals(0f, offset, EPSILON)
    }

    @Test
    fun `forward scroll uses negative raw offset - first half of swipe`() {
        val (page, offset) = normalizePagerPosition(rawPage = 1, rawOffset = -0.3f, itemsCount = ITEMS)

        assertEquals(1, page)
        assertEquals(0.3f, offset, EPSILON)
    }

    @Test
    fun `forward scroll - second half of swipe in center mode`() {
        val (page, offset) = normalizePagerPosition(rawPage = 2, rawOffset = 0.3f, itemsCount = ITEMS)

        assertEquals(1, page)
        assertEquals(0.7f, offset, EPSILON)
    }

    @Test
    fun `positive raw offset on first page is treated as at rest`() {
        val (page, offset) = normalizePagerPosition(rawPage = 0, rawOffset = 0.3f, itemsCount = ITEMS)

        assertEquals(0, page)
        assertEquals(0f, offset, EPSILON)
    }

    @Test
    fun `offset is clamped below 1 even at the boundary`() {
        val (_, forwardOffset) = normalizePagerPosition(rawPage = 0, rawOffset = -1f, itemsCount = ITEMS)
        assertTrue("negative branch must clamp below 1", forwardOffset < 1f)

        val (_, backwardOffset) = normalizePagerPosition(rawPage = 1, rawOffset = 0.000001f, itemsCount = ITEMS)
        assertTrue("positive branch must clamp below 1", backwardOffset < 1f)
    }

    @Test
    fun `out of range raw page is clamped to valid range`() {
        val (page, _) = normalizePagerPosition(rawPage = 99, rawOffset = 0f, itemsCount = ITEMS)
        assertEquals(ITEMS - 1, page)

        val (negPage, _) = normalizePagerPosition(rawPage = -3, rawOffset = 0f, itemsCount = ITEMS)
        assertEquals(0, negPage)
    }

    @Test
    fun `last page with negative offset stays on the last page`() {
        val (page, offset) = normalizePagerPosition(
            rawPage = ITEMS - 1, rawOffset = -0.5f, itemsCount = ITEMS,
        )

        assertEquals(ITEMS - 1, page)
        assertEquals(0.5f, offset, EPSILON)
    }
}
