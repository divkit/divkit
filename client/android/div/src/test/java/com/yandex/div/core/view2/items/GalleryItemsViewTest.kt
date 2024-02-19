package com.yandex.div.core.view2.items

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div2.DivGallery
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [GalleryItemsViewTest.ShadowSmoothScroller::class])
class GalleryItemsViewTest {

    private val layoutManager = mock<LinearLayoutManager> {
        on { itemCount } doReturn ITEM_COUNT
    }
    private val recyclerView = mock<DivRecyclerView> {
        on { layoutManager } doReturn layoutManager
        on { div } doReturn createDivGallery(DivGallery.ScrollMode.DEFAULT)
        on { resources } doReturn mock()
    }

    private val underTest = createUnderTest()

    @Test
    fun `get current item on direction next and has completely visible item in horizontal`() {
        whenCanScrollHorizontally()
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next and has completely visible item in vertical`() {
        whenCanScrollVertically()
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next and no completely visible item position in horizontal`() {
        whenCanScrollHorizontally()
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(RecyclerView.NO_POSITION)
        whenever(layoutManager.findFirstVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next and no completely visible item position in vertical`() {
        whenCanScrollVertically()
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(RecyclerView.NO_POSITION)
        whenever(layoutManager.findFirstVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next with completely visible items when cannot scroll horizontally`() {
        whenCanScrollHorizontally(canScroll = false)
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction previous with completely visible items when cannot scroll horizontally`() {
        val underTest = createUnderTest(direction = Direction.PREVIOUS)
        whenCanScrollHorizontally(canScroll = false)
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }


    @Test
    fun `get current item on direction next without completely visible items when cannot scroll horizontally`() {
        whenCanScrollHorizontally(canScroll = false)
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(RecyclerView.NO_POSITION)
        whenever(layoutManager.findFirstVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next with completely visible items when cannot scroll vertically`() {
        whenCanScrollVertically(canScroll = false)
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction previous with completely visible items when cannot scroll vertically`() {
        val underTest = createUnderTest(direction = Direction.PREVIOUS)
        whenCanScrollVertically(canScroll = false)
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction next without completely visible items when cannot scroll vertically`() {
        whenCanScrollVertically(canScroll = false)
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(RecyclerView.NO_POSITION)
        whenever(layoutManager.findFirstVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `set current item on default scroll mode`() {
        val scrollCaptor = argumentCaptor<LinearSmoothScroller>()

        underTest.currentItem = 5

        verify(layoutManager).startSmoothScroll(scrollCaptor.capture())
        Assert.assertEquals(5, scrollCaptor.firstValue.targetPosition)
    }

    @Test
    fun `get current item on direction previous and has completely visible item`() {
        val underTest = createUnderTest(direction = Direction.PREVIOUS)
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `get current item on direction previous and no completely visible item`() {
        val underTest = createUnderTest(direction = Direction.PREVIOUS)
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(RecyclerView.NO_POSITION)
        whenever(layoutManager.findLastVisibleItemPosition()).thenReturn(4)

        Assert.assertEquals(4, underTest.currentItem)
    }

    @Test
    fun `set current item on snapping recycler view`() {
        val view = mock<DivRecyclerView> {
            on { div } doReturn createDivGallery(DivGallery.ScrollMode.PAGING)
            on { resources } doReturn mock()
        }
        whenever(view.layoutManager).thenReturn(layoutManager)
        val underTest = createUnderTest(drv = view)

        underTest.currentItem = 5

        verify(view).smoothScrollToPosition(5)
    }

    @Test
    fun `item count`() {
        whenever(layoutManager.itemCount).thenReturn(10)

        Assert.assertEquals(10, underTest.itemCount)
    }

    @Test
    fun `set current item when item is not within itemCount`() = disableAssertions {
        underTest.currentItem = 11
        underTest.currentItem = -1

        verify(recyclerView, never()).smoothScrollToPosition(any())
    }

    private fun whenCanScrollHorizontally(canScroll: Boolean = true) {
        whenever(layoutManager.orientation).thenReturn(RecyclerView.HORIZONTAL)
        whenever(recyclerView.canScrollHorizontally(1)).thenReturn(canScroll)
    }

    private fun whenCanScrollVertically(canScroll: Boolean = true) {
        whenever(layoutManager.orientation).thenReturn(RecyclerView.VERTICAL)
        whenever(recyclerView.canScrollVertically(1)).thenReturn(canScroll)
    }

    private fun createUnderTest(
        drv: DivRecyclerView = recyclerView,
        direction: Direction = Direction.NEXT
    ) = DivViewWithItems.create(drv, mock()) { direction }!!

    private fun createDivGallery(scrollMode: DivGallery.ScrollMode): DivGallery =
        DivGallery(scrollMode = scrollMode.asExpression(), items = emptyList())

    @Implements(LinearSmoothScroller::class)
    class ShadowSmoothScroller {

        @Implementation
        protected fun __constructor__(context: Context?) = Unit
    }

    private companion object {
        private const val ITEM_COUNT = 10
    }
}
