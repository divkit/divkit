package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.IntSize
import com.yandex.div.render.common.pager.PagerItemWindow
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DivPagerStateTest {

    @Test
    fun `empty visible items - currentPage keeps initial page with zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            firstVisibleItemIndex = 7,
            visibleItems = emptyList(),
            initialPage = 3,
        )

        assertEquals(3, state.currentPage)
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
            state.currentPageOffsetFraction > 0f,
            "Expected positive fraction, was ${state.currentPageOffsetFraction}"
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
    fun `start mode - item at the start padding has zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            firstVisibleItemIndex = 1,
            visibleItems = listOf(
                item(index = 1, offset = -100, size = 100),
                item(index = 2, offset = 0, size = 100),
            ),
            viewportStart = -60,
            viewportEnd = 240,
        )

        assertEquals(2, state.currentPage)
        assertEquals(0f, state.currentPageOffsetFraction, EPSILON)
    }

    @Test
    fun `end mode - item at the end padding has zero offset`() {
        val state = pagerState(
            snapPosition = SnapPosition.End,
            firstVisibleItemIndex = 2,
            visibleItems = listOf(
                item(index = 2, offset = 80, size = 100),
                item(index = 3, offset = 180, size = 100),
            ),
            viewportStart = -60,
            viewportEnd = 240,
            afterContentPadding = 60,
        )

        assertEquals(2, state.currentPage)
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

    @Test
    fun `infinite window reports first real page for its first middle item`() {
        val itemWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            visibleItems = listOf(item(index = itemWindow.rawIndex(0), offset = 0, size = 100)),
            infiniteScroll = true,
        )

        assertEquals(0, state.currentPage)
    }

    @Test
    fun `infinite window reports last real page for leading edge copy`() {
        val itemWindow = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
        val state = pagerState(
            snapPosition = SnapPosition.Start,
            visibleItems = listOf(item(index = itemWindow.rawIndex(0) - 1, offset = 0, size = 100)),
            infiniteScroll = true,
        )

        assertEquals(4, state.currentPage)
    }

    @Test
    fun `layout snapshot - page visible in both layouts with a new size is a resize`() {
        val previous = PagerLayoutSnapshot(page = 2, itemSizes = listOf(1 to 100, 2 to 100))

        assertTrue(PagerLayoutSnapshot(page = 2, itemSizes = listOf(1 to 100, 2 to 160)).isResizeOf(previous))
    }

    @Test
    fun `layout snapshot - other visible pages of the same size are not a resize`() {
        val previous = PagerLayoutSnapshot(page = 2, itemSizes = listOf(1 to 100, 2 to 100))

        assertFalse(PagerLayoutSnapshot(page = 3, itemSizes = listOf(2 to 100, 3 to 50)).isResizeOf(previous))
    }

    @Test
    fun `layout snapshot - scrolling layout without sizes is not a resize`() {
        val previous = PagerLayoutSnapshot(page = 2, itemSizes = listOf(1 to 100, 2 to 100))

        assertFalse(PagerLayoutSnapshot(page = 2, itemSizes = emptyList()).isResizeOf(previous))
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
            afterContentPadding: Int = 0,
            infiniteScroll: Boolean = false,
            initialPage: Int = 0,
        ): DivPagerState {
            val layoutInfo = object : LazyListLayoutInfo {
                override val visibleItemsInfo = visibleItems
                override val viewportStartOffset = viewportStart
                override val viewportEndOffset = viewportEnd
                override val totalItemsCount = 5
                override val viewportSize = IntSize(viewportEnd - viewportStart, 0)
                override val orientation = Orientation.Horizontal
                override val beforeContentPadding = -viewportStart
                override val afterContentPadding = afterContentPadding
            }
            val listState: LazyListState = mock {
                on { this.layoutInfo } doReturn layoutInfo
                on { this.firstVisibleItemIndex } doReturn firstVisibleItemIndex
            }
            return DivPagerState(
                pageCount = 5,
                listState = listState,
                snapPosition = snapPosition,
                initialPage = initialPage,
                infiniteScroll = infiniteScroll,
            )
        }

        fun item(index: Int, offset: Int, size: Int): LazyListItemInfo = mock {
            on { this.index } doReturn index
            on { this.offset } doReturn offset
            on { this.size } doReturn size
        }
    }
}
