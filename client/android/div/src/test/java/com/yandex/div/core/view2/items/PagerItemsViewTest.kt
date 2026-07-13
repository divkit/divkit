package com.yandex.div.core.view2.items

import android.content.res.Resources
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PagerItemsViewTest {

    private val adapter = mock<DivPagerAdapter> {
        on { itemCount } doReturn ITEM_COUNT
    }
    private val viewPager = mock<ViewPager2> {
        on { adapter } doReturn adapter
        on { currentItem } doReturn CURRENT_ITEM
    }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn mock()
    }
    private val view = mock<DivPagerView> {
        on { viewPager } doReturn viewPager
        on { resources } doReturn resources
    }

    private val underTest = DivViewWithItems.Pager(view)

    @Test
    fun `item count`() {
        assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `set current item`() {
        underTest.setCurrentItem(5, true)
        verify(viewPager).setCurrentItem(5, true)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.setCurrentItem(-5, true)
        underTest.setCurrentItem(ITEM_COUNT, true)

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
}

private const val CURRENT_ITEM = 5
private const val ITEM_COUNT = 10
