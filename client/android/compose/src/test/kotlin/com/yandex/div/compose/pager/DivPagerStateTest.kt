package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class DivPagerStateTest {

    @Test
    fun `empty visible items - currentPage falls back to firstVisibleItemIndex with zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            firstVisibleItemIndex = 7,
            visibleItems = emptyList(),
        )

        assertEquals(7, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `start mode - first item flush against viewport start has zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            visibleItems = listOf(item(index = 0, offset = 0, size = 100)),
        )

        assertEquals(0, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `start mode - partial scroll yields negative offset proportional to progress`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            visibleItems = listOf(
                item(index = 0, offset = -30, size = 100),
                item(index = 1, offset = 70, size = 100),
            ),
        )

        assertEquals(0, state.currentPage)
        assertEquals(-0.3f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `start mode - scrolled to next item reports that index`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            firstVisibleItemIndex = 2,
            visibleItems = listOf(item(index = 2, offset = 0, size = 100)),
        )

        assertEquals(2, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `center mode - item centered yields zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Center,
            firstVisibleItemIndex = 2,
            visibleItems = listOf(item(index = 2, offset = 0, size = 100)),
        )

        assertEquals(2, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `center mode - past midpoint reports next page with positive offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Center,
            firstVisibleItemIndex = 1,
            visibleItems = listOf(
                item(index = 1, offset = -60, size = 100),
                item(index = 2, offset = 40, size = 100),
            ),
        )

        assertEquals(2, state.currentPage)
        assertTrue(
            "expected positive fraction, was ${state.currentPageOffsetFraction}",
            state.currentPageOffsetFraction > 0f,
        )
        assertEquals(0.4f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `center mode - before midpoint stays on current page with negative offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Center,
            firstVisibleItemIndex = 1,
            visibleItems = listOf(
                item(index = 1, offset = -30, size = 100),
                item(index = 2, offset = 70, size = 100),
            ),
        )

        assertEquals(1, state.currentPage)
        assertEquals(-0.3f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `end mode - item right-aligned yields zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.End,
            firstVisibleItemIndex = 3,
            visibleItems = listOf(item(index = 3, offset = 0, size = 100)),
        )

        assertEquals(3, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `zero-sized snapped item returns zero fraction without dividing by zero`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            visibleItems = listOf(item(index = 0, offset = 0, size = 0)),
        )

        assertEquals(0, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    private companion object {
        const val EPSILON = 1e-4f
        const val VIEWPORT_END = 100

        fun pagerState(
            snapPosition: SnapPosition,
            visibleItems: List<LazyListItemInfo>,
            firstVisibleItemIndex: Int = 0,
            viewportStart: Int = 0,
            viewportEnd: Int = VIEWPORT_END,
        ): DivPagerState {
            val layoutInfo: LazyListLayoutInfo = mock {
                on { this.visibleItemsInfo } doReturn visibleItems
                on { this.viewportStartOffset } doReturn viewportStart
                on { this.viewportEndOffset } doReturn viewportEnd
                on { this.totalItemsCount } doReturn 5
            }
            val listState: LazyListState = mock {
                on { this.layoutInfo } doReturn layoutInfo
                on { this.firstVisibleItemIndex } doReturn firstVisibleItemIndex
            }
            return DivPagerState(pageCount = 5, listState = listState, snapPosition = snapPosition)
        }

        fun item(index: Int, offset: Int, size: Int): LazyListItemInfo = mock {
            on { this.index } doReturn index
            on { this.offset } doReturn offset
            on { this.size } doReturn size
        }
    }
}
