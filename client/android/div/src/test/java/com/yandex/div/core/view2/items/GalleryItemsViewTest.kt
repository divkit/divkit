package com.yandex.div.core.view2.items

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
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

@RunWith(RobolectricTestRunner::class)
class GalleryItemsViewTest {

    private val layoutManager = mock<DivLinearLayoutManager>()
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn mock()
    }
    private val context = mock<Context> {
        on { resources } doReturn resources
    }
    private val adapter = mock<DivGalleryAdapter> {
        on { itemCount } doReturn ITEM_COUNT
    }
    private val recyclerView = mock<DivRecyclerView> {
        on { layoutManager } doReturn layoutManager
        on { context } doReturn context
        on { resources } doReturn resources
        on { adapter } doReturn adapter
    }
    private val scrollCaptor = argumentCaptor<LinearSmoothScroller>()

    private val underTest = DivViewWithItems.Gallery(recyclerView)

    @Test
    fun `item count`() {
        Assert.assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `set current item with animation`() {
        underTest.setCurrentItem(5, true)

        verify(layoutManager).startSmoothScroll(scrollCaptor.capture())
        Assert.assertEquals(5, scrollCaptor.firstValue.targetPosition)
    }

    @Test
    fun `set current item without animation`() {
        underTest.setCurrentItem(5, false)
        verify(layoutManager).instantScroll(5)
    }

    @Test
    fun `set current item when item is not within itemCount`() = disableAssertions {
        underTest.setCurrentItem(11, true)
        underTest.setCurrentItem(-1, true)

        verify(layoutManager, never()).startSmoothScroll(any())
        verify(layoutManager, never()).instantScroll(any(), any())
    }

    @Test
    fun `get next item in vertical gallery`() {
        underTest.getNearestItem(ScrollDirection.NEXT)
        verify(layoutManager).getNearestItemPosition(ScrollDirection.NEXT.value)
    }

    @Test
    fun `get previous item in vertical gallery`() {
        underTest.getNearestItem(ScrollDirection.PREVIOUS)
        verify(layoutManager).getNearestItemPosition(ScrollDirection.PREVIOUS.value)
    }

    @Test
    fun `get next item in ltr gallery`() {
        whenever(layoutManager.isHorizontal).doReturn(true)
        underTest.getNearestItem(ScrollDirection.NEXT)
        verify(layoutManager).getNearestItemPosition(ScrollDirection.NEXT.value)
    }

    @Test
    fun `get previous item in ltr gallery`() {
        whenever(layoutManager.isHorizontal).doReturn(true)
        underTest.getNearestItem(ScrollDirection.PREVIOUS)
        verify(layoutManager).getNearestItemPosition(ScrollDirection.PREVIOUS.value)
    }

    @Test
    fun `get next item in rtl gallery`() {
        whenever(layoutManager.isHorizontal).doReturn(true)
        whenever(recyclerView.layoutDirection).doReturn(View.LAYOUT_DIRECTION_RTL)

        underTest.getNearestItem(ScrollDirection.NEXT)

        verify(layoutManager).getNearestItemPosition(ScrollDirection.PREVIOUS.value)
    }

    @Test
    fun `get previous item in rtl gallery`() {
        whenever(layoutManager.isHorizontal).doReturn(true)
        whenever(recyclerView.layoutDirection).doReturn(View.LAYOUT_DIRECTION_RTL)

        underTest.getNearestItem(ScrollDirection.PREVIOUS)

        verify(layoutManager).getNearestItemPosition(ScrollDirection.NEXT.value)
    }

    private companion object {
        private const val ITEM_COUNT = 10
    }
}
