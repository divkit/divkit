package com.yandex.div.compose.pager

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PagerWindowStateTest {

    private val finiteWindow = PagerItemWindow(realItemCount = 5)
    private val infiniteWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)

    @Test
    fun `position is not classified before lazy list layout`() {
        val state = PagerWindowState(finiteWindow, initialRealPage = 3)

        assertNull(
            state.update(
                itemWindow = infiniteWindow,
                rawPage = 0,
                isPositionAvailable = false,
            )
        )

        assertEquals(
            infiniteWindow.rawIndex(3),
            state.update(
                itemWindow = infiniteWindow,
                rawPage = 0,
                isPositionAvailable = true,
            )
        )
    }

    @Test
    fun `toggle in both directions preserves nonzero real page`() {
        val state = PagerWindowState(finiteWindow, initialRealPage = 0)
        assertNull(state.update(finiteWindow, rawPage = 3, isPositionAvailable = true))

        val infiniteRawPage = state.update(infiniteWindow, rawPage = 3, isPositionAvailable = true)
        assertEquals(infiniteWindow.rawIndex(3), infiniteRawPage)
        assertNull(state.update(infiniteWindow, rawPage = infiniteRawPage!!, isPositionAvailable = true))

        assertEquals(
            finiteWindow.rawIndex(3),
            state.update(finiteWindow, rawPage = infiniteRawPage, isPositionAvailable = true)
        )
    }
}
