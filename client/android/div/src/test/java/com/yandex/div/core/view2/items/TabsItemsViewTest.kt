package com.yandex.div.core.view2.items

import android.content.res.Resources
import androidx.viewpager.widget.PagerAdapter
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.widget.tabs.ScrollableViewPager
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class TabsItemsViewTest {

    private val adapter = mock<PagerAdapter> {
        on { count } doReturn ITEM_COUNT
    }
    private val viewPager = mock<ScrollableViewPager> {
        on { adapter } doReturn adapter
        on { currentItem } doReturn CURRENT_ITEM
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
        Assert.assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `set current item`() {
        underTest.setCurrentItem(5, true)
        verify(viewPager).setCurrentItem(5, true)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.setCurrentItem(ITEM_COUNT, true)
        underTest.setCurrentItem(-1, true)

        verify(viewPager, never()).setCurrentItem(any(), any())
    }

    @Test
    fun `get next item`() {
        assertEquals(CURRENT_ITEM + 1, underTest.getNearestItem(ScrollDirection.NEXT))
    }

    @Test
    fun `get previous item`() {
        assertEquals(CURRENT_ITEM - 1, underTest.getNearestItem(ScrollDirection.PREVIOUS))
    }

    private companion object {
        private const val CURRENT_ITEM = 5
        private const val ITEM_COUNT = 10
    }
}
