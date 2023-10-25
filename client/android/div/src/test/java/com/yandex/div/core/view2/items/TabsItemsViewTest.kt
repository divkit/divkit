package com.yandex.div.core.view2.items

import androidx.viewpager.widget.PagerAdapter
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.Assert
import com.yandex.div.internal.widget.tabs.ScrollableViewPager
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TabsItemsViewTest {

    private val adapter = mock<PagerAdapter> {
        on { count } doReturn ITEM_COUNT
    }
    private val viewPager = mock<ScrollableViewPager> {
        on { adapter } doReturn adapter
    }
    private val tabsLayout = mock<DivTabsLayout> {
        on { viewPager } doReturn viewPager
    }
    private val underTest = DivViewWithItems.Tabs(tabsLayout)

    @Test
    fun `item count`() {
        Assert.assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `get current item`() {
        whenever(viewPager.currentItem).thenReturn(5)

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `set current item`() {
        underTest.currentItem = 5

        verify(viewPager).setCurrentItem(5, true)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.currentItem = ITEM_COUNT
        underTest.currentItem = -1

        verify(viewPager, never()).setCurrentItem(any(), any())
    }

    private companion object {
        private const val ITEM_COUNT = 10
    }
}
