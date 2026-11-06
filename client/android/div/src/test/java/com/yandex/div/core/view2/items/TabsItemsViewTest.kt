package com.yandex.div.core.view2.items

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.widget.tabs.ScrollableViewPager
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class TabsItemsViewTest {

    private val adapter = mock<RecyclerView.Adapter<*>> {
        on { itemCount } doReturn ITEM_COUNT
    }
    private val pager = mock<ViewPager2> {
        on { scrollState } doReturn SCROLL_STATE_IDLE
    }
    private val viewPager = mock<ScrollableViewPager> {
        on { adapter } doReturn adapter
        on { currentItem } doReturn CURRENT_ITEM
        on { viewPager } doReturn pager
    }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn mock()
    }
    private val tabsLayout = mock<DivTabsLayout> {
        on { viewPager } doReturn viewPager
        on { resources } doReturn resources
    }
    private val underTest = DivViewWithItems.Tabs(tabsLayout)

    @Test
    fun `item count`() {
        assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `set current item is animated when pager is idle`() {
        underTest.setCurrentItem(5, true)

        verify(viewPager).scrollToItem(5, null)
    }

    @Test
    fun `set current item keeps animation when pager is settling`() {
        whenever(pager.scrollState).thenReturn(SCROLL_STATE_SETTLING)

        underTest.setCurrentItem(5, true)

        verify(viewPager).scrollToItem(5, null)
    }

    @Test
    fun `set current item keeps animation when pager is dragging`() {
        whenever(pager.scrollState).thenReturn(SCROLL_STATE_DRAGGING)

        underTest.setCurrentItem(5, true)

        verify(viewPager).scrollToItem(5, null)
    }

    @Test
    fun `set current item stays not animated when pager is idle`() {
        underTest.setCurrentItem(5, false)

        verify(viewPager).scrollToItem(5, 0)
    }

    @Test
    fun `scroll to end keeps animation when pager is settling`() {
        whenever(pager.scrollState).thenReturn(SCROLL_STATE_SETTLING)

        underTest.scrollToTheEnd(true)

        verify(viewPager).scrollToItem(ITEM_COUNT - 1, null)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.setCurrentItem(ITEM_COUNT, true)
        underTest.setCurrentItem(-1, true)

        verify(viewPager, never()).scrollToItem(any(), anyOrNull())
    }

    @Test
    fun `get next item`() {
        assertEquals(CURRENT_ITEM + 1, underTest.getNearestItem(ScrollDirection.NEXT))
    }

    @Test
    fun `get previous item`() {
        assertEquals(CURRENT_ITEM - 1, underTest.getNearestItem(ScrollDirection.PREVIOUS))
    }

    @Test
    fun `animated scroll forwards duration`() {
        underTest.setCurrentItem(2, animated = true, duration = 2000)

        verify(viewPager).scrollToItem(2, 2000)
    }

    @Test
    fun `instant scroll ignores positive duration`() {
        underTest.setCurrentItem(2, animated = false, duration = 2000)

        verify(viewPager).scrollToItem(2, 0)
    }

    @Test
    fun `zero duration is forwarded unchanged`() {
        underTest.setCurrentItem(2, animated = true, duration = 0)

        verify(viewPager).scrollToItem(2, 0)
    }

    private companion object {
        private const val CURRENT_ITEM = 5
        private const val ITEM_COUNT = 10
    }
}
