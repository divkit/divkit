package com.yandex.div.core.view2.divs.gallery

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div2.DivGallery
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivGridLayoutManagerTest {

    private val layoutManager = spy(
        DivGridLayoutManager(
            view = mock<DivRecyclerView>(),
            divView = mock<Div2View>(),
            orientation = RecyclerView.HORIZONTAL,
            crossContentAlignment = DivGallery.ContentAlignment.START,
            columnCount = SPAN_COUNT,
            itemSpacing = 0,
            crossSpacing = 0,
        )
    )

    @Test
    fun `visible item positions use span count and correct extremes`() {
        stubPositions(
            firstCompletelyVisible = intArrayOf(5, 3),
            lastCompletelyVisible = intArrayOf(8, 10),
            firstVisible = intArrayOf(7, 5),
            lastVisible = intArrayOf(6, 9),
        )

        assertEquals(3, layoutManager.firstCompletelyVisibleItemPosition())
        assertEquals(10, layoutManager.lastCompletelyVisibleItemPosition())
        assertEquals(5, layoutManager.firstVisibleItemPosition())
        assertEquals(9, layoutManager.lastVisibleItemPosition())
    }

    @Test
    fun `visible item positions ignore no position`() {
        stubPositions(
            firstCompletelyVisible = intArrayOf(RecyclerView.NO_POSITION, 4),
            lastCompletelyVisible = intArrayOf(RecyclerView.NO_POSITION, 8),
            firstVisible = intArrayOf(6, RecyclerView.NO_POSITION),
            lastVisible = intArrayOf(9, RecyclerView.NO_POSITION),
        )

        assertEquals(4, layoutManager.firstCompletelyVisibleItemPosition())
        assertEquals(8, layoutManager.lastCompletelyVisibleItemPosition())
        assertEquals(6, layoutManager.firstVisibleItemPosition())
        assertEquals(9, layoutManager.lastVisibleItemPosition())
    }

    @Test
    fun `visible item positions return no position when no items are visible`() {
        val noPositions = IntArray(SPAN_COUNT) { RecyclerView.NO_POSITION }
        stubPositions(noPositions, noPositions, noPositions, noPositions)

        assertEquals(RecyclerView.NO_POSITION, layoutManager.firstCompletelyVisibleItemPosition())
        assertEquals(RecyclerView.NO_POSITION, layoutManager.lastCompletelyVisibleItemPosition())
        assertEquals(RecyclerView.NO_POSITION, layoutManager.firstVisibleItemPosition())
        assertEquals(RecyclerView.NO_POSITION, layoutManager.lastVisibleItemPosition())
    }

    private fun stubPositions(
        firstCompletelyVisible: IntArray,
        lastCompletelyVisible: IntArray,
        firstVisible: IntArray,
        lastVisible: IntArray,
    ) {
        doAnswer { fillPositions(it.getArgument(0), firstCompletelyVisible) }
            .whenever(layoutManager).findFirstCompletelyVisibleItemPositions(any())
        doAnswer { fillPositions(it.getArgument(0), lastCompletelyVisible) }
            .whenever(layoutManager).findLastCompletelyVisibleItemPositions(any())
        doAnswer { fillPositions(it.getArgument(0), firstVisible) }
            .whenever(layoutManager).findFirstVisibleItemPositions(any())
        doAnswer { fillPositions(it.getArgument(0), lastVisible) }
            .whenever(layoutManager).findLastVisibleItemPositions(any())
    }

    private fun fillPositions(target: IntArray, positions: IntArray): IntArray {
        assertEquals(SPAN_COUNT, target.size)
        positions.copyInto(target)
        return target
    }

    private companion object {
        const val SPAN_COUNT = 2
    }
}
