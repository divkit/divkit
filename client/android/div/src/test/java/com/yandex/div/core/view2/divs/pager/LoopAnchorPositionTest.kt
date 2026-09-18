package com.yandex.div.core.view2.divs.pager

import androidx.recyclerview.widget.RecyclerView
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Positions of an infinite pager with 5 real items and 20 virtual items on each side:
 * 0..19 repeat the tail, 20..24 are real, 25..44 repeat the head.
 */
class LoopAnchorPositionTest {

    @Test
    fun `no anchor while the pager stays within the real items`() {
        assertNull(anchorFor(firstItemVisible = 22, lastItemVisible = 23, scrollDelta = 1))
        assertNull(anchorFor(firstItemVisible = 20, lastItemVisible = 21, scrollDelta = -1))
    }

    @Test
    fun `forward scroll anchors on the first virtual item after the real ones`() {
        assertEquals(25, anchorFor(firstItemVisible = 25, lastItemVisible = 26, scrollDelta = 1))
    }

    @Test
    fun `forward fling anchors on the item it actually reached, not on the seam`() {
        // A single fling frame can advance several pages at once. Re-anchoring on the seam would
        // move the content back by the overshoot instead of exactly one loop.
        assertEquals(27, anchorFor(firstItemVisible = 27, lastItemVisible = 28, scrollDelta = 900))
    }

    @Test
    fun `backward scroll anchors on the last virtual item before the real ones`() {
        assertEquals(19, anchorFor(firstItemVisible = 18, lastItemVisible = 19, scrollDelta = -1))
    }

    @Test
    fun `backward fling anchors on the item it actually reached`() {
        assertEquals(17, anchorFor(firstItemVisible = 16, lastItemVisible = 17, scrollDelta = -900))
    }

    @Test
    fun `scroll direction opposite to the crossed edge is ignored`() {
        assertNull(anchorFor(firstItemVisible = 25, lastItemVisible = 26, scrollDelta = -1))
        assertNull(anchorFor(firstItemVisible = 18, lastItemVisible = 19, scrollDelta = 1))
    }

    @Test
    fun `idle frame never loops`() {
        assertNull(anchorFor(firstItemVisible = 25, lastItemVisible = 26, scrollDelta = 0))
    }

    @Test
    fun `missing positions never loop`() {
        assertNull(
            anchorFor(
                firstItemVisible = RecyclerView.NO_POSITION,
                lastItemVisible = RecyclerView.NO_POSITION,
                scrollDelta = -1,
            )
        )
    }

    private fun anchorFor(firstItemVisible: Int, lastItemVisible: Int, scrollDelta: Int) =
        loopAnchorPosition(
            firstItemVisible = firstItemVisible,
            lastItemVisible = lastItemVisible,
            scrollDelta = scrollDelta,
            itemCount = REAL_ITEM_COUNT + VIRTUAL_ITEM_COUNT * 2,
            virtualItemCount = VIRTUAL_ITEM_COUNT,
        )

    private companion object {
        private const val REAL_ITEM_COUNT = 5
        private const val VIRTUAL_ITEM_COUNT = 20
    }
}
