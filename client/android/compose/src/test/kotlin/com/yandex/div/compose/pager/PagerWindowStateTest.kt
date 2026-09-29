package com.yandex.div.compose.pager

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PagerWindowStateTest {

    private val finiteWindow = PagerItemWindow(realItemCount = 5)
    private val infiniteWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
    private val itemKeys = listOf(0L, 1L, 2L, 3L, 4L)
    private val infiniteState = PagerWindowState(infiniteWindow, initialRealPage = 0, itemKeys)

    @Test
    fun `offscreen patch preserves the visible page in the next cycle`() {
        val rawPage = infiniteWindow.rawIndex(2) + 5
        infiniteState.update(infiniteWindow, rawPage, isPositionAvailable = true, itemKeys)

        val restoredPage = infiniteState.update(
            infiniteWindow, rawPage, isPositionAvailable = true, itemKeys = listOf(10L, 1L, 2L, 3L, 4L)
        )

        assertEquals(rawPage, restoredPage)
    }

    @Test
    fun `offscreen patch preserves the visible page in the previous cycle`() {
        val rawPage = infiniteWindow.rawIndex(2) - 5
        infiniteState.update(infiniteWindow, rawPage, isPositionAvailable = true, itemKeys)

        val restoredPage = infiniteState.update(
            infiniteWindow, rawPage, isPositionAvailable = true, itemKeys = listOf(10L, 1L, 2L, 3L, 4L)
        )

        assertEquals(rawPage, restoredPage)
    }

    @Test
    fun `removing a preceding page preserves the visible key and positive cycle`() {
        val rawPage = infiniteWindow.rawIndex(2) + 10
        infiniteState.update(infiniteWindow, rawPage, isPositionAvailable = true, itemKeys)
        val smallerWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 4)

        val restoredPage = infiniteState.update(
            smallerWindow, rawPage, isPositionAvailable = true, itemKeys = listOf(1L, 2L, 3L, 4L)
        )

        assertEquals(smallerWindow.rawIndex(1) + 8, restoredPage)
    }

    @Test
    fun `inserting a preceding page preserves the visible key and negative cycle`() {
        val rawPage = infiniteWindow.rawIndex(2) - 10
        infiniteState.update(infiniteWindow, rawPage, isPositionAvailable = true, itemKeys)
        val largerWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 6)

        val restoredPage = infiniteState.update(
            largerWindow, rawPage, isPositionAvailable = true, itemKeys = listOf(10L, 0L, 1L, 2L, 3L, 4L)
        )

        assertEquals(largerWindow.rawIndex(3) - 12, restoredPage)
    }

    @Test
    fun `disabling infinite scroll restores the real page after wrapping`() {
        val rawPage = infiniteWindow.rawIndex(2) + 5
        infiniteState.update(infiniteWindow, rawPage, isPositionAvailable = true, itemKeys)

        val restoredPage = infiniteState.update(finiteWindow, rawPage, isPositionAvailable = true, itemKeys)

        assertEquals(2, restoredPage)
    }

    @Test
    fun `cycle is clamped at the upper edge without changing the real page`() {
        val oldWindow = PagerItemWindow(realItemCount = 5, edgeItemCount = 10)
        val newWindow = PagerItemWindow(realItemCount = 6, edgeItemCount = 6)
        val state = PagerWindowState(oldWindow, initialRealPage = 0, itemKeys)
        state.update(oldWindow, rawPage = 22, isPositionAvailable = true, itemKeys)

        val restoredPage = state.update(
            newWindow, rawPage = 22, isPositionAvailable = true, itemKeys = listOf(0L, 1L, 2L, 3L, 4L, 5L)
        )

        assertEquals(14, restoredPage)
    }

    @Test
    fun `cycle is clamped at the lower edge without changing the real page`() {
        val oldWindow = PagerItemWindow(realItemCount = 5, edgeItemCount = 10)
        val newWindow = PagerItemWindow(realItemCount = 6, edgeItemCount = 6)
        val state = PagerWindowState(oldWindow, initialRealPage = 0, itemKeys)
        state.update(oldWindow, rawPage = 2, isPositionAvailable = true, itemKeys)

        val restoredPage = state.update(
            newWindow, rawPage = 2, isPositionAvailable = true, itemKeys = listOf(0L, 1L, 2L, 3L, 4L, 5L)
        )

        assertEquals(2, restoredPage)
    }

    @Test
    fun `position is not classified before lazy list layout`() {
        val state = PagerWindowState(finiteWindow, initialRealPage = 3, itemKeys)

        assertNull(
            state.update(
                itemWindow = infiniteWindow,
                rawPage = 0,
                isPositionAvailable = false,
                itemKeys = itemKeys,
            )
        )

        assertEquals(
            infiniteWindow.rawIndex(3),
            state.update(
                itemWindow = infiniteWindow,
                rawPage = 0,
                isPositionAvailable = true,
                itemKeys = itemKeys,
            )
        )
    }

    @Test
    fun `toggle in both directions preserves nonzero real page`() {
        val state = PagerWindowState(finiteWindow, initialRealPage = 0, itemKeys)
        assertNull(state.update(finiteWindow, rawPage = 3, isPositionAvailable = true, itemKeys))

        val infiniteRawPage = state.update(infiniteWindow, rawPage = 3, isPositionAvailable = true, itemKeys)
        assertEquals(infiniteWindow.rawIndex(3), infiniteRawPage)
        assertNull(state.update(infiniteWindow, rawPage = infiniteRawPage!!, isPositionAvailable = true, itemKeys))

        assertEquals(
            finiteWindow.rawIndex(3),
            state.update(finiteWindow, rawPage = infiniteRawPage, isPositionAvailable = true, itemKeys)
        )
    }
}
