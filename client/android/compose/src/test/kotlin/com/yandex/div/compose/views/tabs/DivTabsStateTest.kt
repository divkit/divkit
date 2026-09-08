package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.pager.PagerState
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivTabsStateTest {

    @Test
    fun `tiny offset on the first page keeps its exact position`() {
        assertEquals(0f, position(page = 0, offset = 0.000001f))
    }

    @Test
    fun `tiny positive offset keeps the current page position`() {
        assertEquals(1f, position(page = 1, offset = 0.000001f))
    }

    @Test
    fun `tiny negative offset keeps the current page position`() {
        assertEquals(1f, position(page = 1, offset = -0.000001f))
    }

    @Test
    fun `forward swipe preserves its fractional position`() {
        assertEquals(1.25f, position(page = 1, offset = 0.25f))
    }

    @Test
    fun `backward swipe preserves its fractional position`() {
        assertEquals(0.75f, position(page = 1, offset = -0.25f))
    }

    private fun position(page: Int, offset: Float): Float {
        return PagerState(
            currentPage = page,
            currentPageOffsetFraction = offset,
            pageCount = { 3 },
        ).logicalPosition
    }
}
