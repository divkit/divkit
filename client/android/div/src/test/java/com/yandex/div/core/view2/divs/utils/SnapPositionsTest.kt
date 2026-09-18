package com.yandex.div.core.view2.divs.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class SnapPositionsTest {

    @Test
    fun `snap is forward when vertical velocity is positive`() {
        val result = isForwardScroll(isVertical = true, velocityX = -10, velocityY = 10, isRtl = false)
        assertTrue(result)
    }

    @Test
    fun `snap is backward when vertical velocity is negative`() {
        val result = isForwardScroll(isVertical = true, velocityX = 10, velocityY = -10, isRtl = false)
        assertFalse(result)
    }

    @Test
    fun `snap is forward when horizontal velocity is positive in ltr`() {
        val result = isForwardScroll(isVertical = false, velocityX = 10, velocityY = -10, isRtl = false)
        assertTrue(result)
    }

    @Test
    fun `snap is backward when horizontal velocity is positive in rtl`() {
        val result = isForwardScroll(isVertical = false, velocityX = 10, velocityY = -10, isRtl = true)
        assertFalse(result)
    }

    @Test
    fun `last completely visible item is selected when snapping forward`() {
        val result = findDirectionalSnapPosition(
            forward = true,
            positions = snapPositions(
                itemCount = 10,
                firstCompletelyVisible = 1,
                lastCompletelyVisible = 2,
                firstVisible = 0,
                lastVisible = 3,
            ),
        )

        assertEquals(2, result)
    }

    @Test
    fun `first completely visible item is selected when snapping backward`() {
        val result = findDirectionalSnapPosition(
            forward = false,
            positions = snapPositions(
                itemCount = 10,
                firstCompletelyVisible = 2,
                lastCompletelyVisible = 3,
                firstVisible = 1,
                lastVisible = 4,
            ),
        )

        assertEquals(2, result)
    }

    @Test
    fun `visible item is selected when no item is completely visible`() {
        val result = findDirectionalSnapPosition(
            forward = true,
            positions = snapPositions(
                itemCount = 10,
                firstCompletelyVisible = RecyclerView.NO_POSITION,
                lastCompletelyVisible = RecyclerView.NO_POSITION,
                firstVisible = 1,
                lastVisible = 2,
            ),
        )

        assertEquals(2, result)
    }

    @Test
    fun `first position is selected when no item is visible`() {
        val result = findDirectionalSnapPosition(
            forward = false,
            positions = snapPositions(
                itemCount = 0,
                firstCompletelyVisible = RecyclerView.NO_POSITION,
                lastCompletelyVisible = RecyclerView.NO_POSITION,
                firstVisible = RecyclerView.NO_POSITION,
                lastVisible = RecyclerView.NO_POSITION,
            ),
        )

        assertEquals(0, result)
    }

    @Test
    fun `last visible item is selected when snapping forward at linear layout end`() {
        val result = findDirectionalSnapPosition(
            forward = true,
            positions = snapPositions(
                itemCount = 4,
                firstCompletelyVisible = 1,
                lastCompletelyVisible = 2,
                firstVisible = 0,
                lastVisible = 3,
            ),
        )

        assertEquals(3, result)
    }

    @Test
    fun `first visible item is selected when snapping backward at linear layout start`() {
        val result = findDirectionalSnapPosition(
            forward = false,
            positions = snapPositions(
                itemCount = 4,
                firstCompletelyVisible = 1,
                lastCompletelyVisible = 2,
                firstVisible = 0,
                lastVisible = 3,
            ),
        )

        assertEquals(0, result)
    }

    @Test
    fun `last completely visible item is selected when snapping forward at grid end`() {
        val result = findDirectionalSnapPosition(
            forward = true,
            positions = snapPositions(
                itemCount = 4,
                isLinearLayout = false,
                firstCompletelyVisible = 1,
                lastCompletelyVisible = 2,
                firstVisible = 0,
                lastVisible = 3,
            ),
        )

        assertEquals(2, result)
    }

    @Test
    fun `first completely visible item is selected when snapping backward at grid start`() {
        val result = findDirectionalSnapPosition(
            forward = false,
            positions = snapPositions(
                itemCount = 4,
                isLinearLayout = false,
                firstCompletelyVisible = 1,
                lastCompletelyVisible = 2,
                firstVisible = 0,
                lastVisible = 3,
            ),
        )

        assertEquals(1, result)
    }

    @Test
    fun `first position is selected when snapping forward in empty layout`() {
        val result = findDirectionalSnapPosition(
            forward = true,
            positions = snapPositions(
                itemCount = 0,
                firstCompletelyVisible = RecyclerView.NO_POSITION,
                lastCompletelyVisible = RecyclerView.NO_POSITION,
                firstVisible = RecyclerView.NO_POSITION,
                lastVisible = RecyclerView.NO_POSITION,
            ),
        )

        assertEquals(0, result)
    }

    @Test
    fun `linear layout manager positions are mapped to matching finders`() {
        val manager = mock<LinearLayoutManager> {
            on { itemCount } doReturn 10
            on { findFirstCompletelyVisibleItemPosition() } doReturn 1
            on { findLastCompletelyVisibleItemPosition() } doReturn 2
            on { findFirstVisibleItemPosition() } doReturn 0
            on { findLastVisibleItemPosition() } doReturn 3
        }

        val positions = manager.snapPositions()

        assertEquals(10, positions.itemCount)
        assertTrue(positions.isLinearLayout)
        assertEquals(1, positions.firstCompletelyVisible)
        assertEquals(2, positions.lastCompletelyVisible)
        assertEquals(0, positions.firstVisible)
        assertEquals(3, positions.lastVisible)
    }

    private fun snapPositions(
        itemCount: Int,
        firstCompletelyVisible: Int,
        lastCompletelyVisible: Int,
        firstVisible: Int,
        lastVisible: Int,
        isLinearLayout: Boolean = true,
    ): SnapPositions {
        val items = itemCount
        val linear = isLinearLayout
        val firstComplete = firstCompletelyVisible
        val lastComplete = lastCompletelyVisible
        val first = firstVisible
        val last = lastVisible
        return object : SnapPositions {
            override val itemCount = items
            override val isLinearLayout = linear
            override val firstCompletelyVisible = firstComplete
            override val lastCompletelyVisible = lastComplete
            override val firstVisible = first
            override val lastVisible = last
        }
    }
}
