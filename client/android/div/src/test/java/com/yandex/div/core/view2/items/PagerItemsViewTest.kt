package com.yandex.div.core.view2.items

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PagerItemsViewTest {

    private val viewPager = ViewPager2(ApplicationProvider.getApplicationContext()).apply {
        adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn ITEM_COUNT
        }
    }

    private val view = mock<DivPagerView> {
        on { viewPager } doReturn viewPager
        on { resources } doReturn mock()
    }

    private val underTest = DivViewWithItems.Pager(view)

    @Test
    fun `item count`() {
        assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `get current item`() {
        viewPager.currentItem = 5

        assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `set current item`() {
        underTest.currentItem = 5

        assertEquals(5, viewPager.currentItem)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.currentItem = -5
        underTest.currentItem = ITEM_COUNT

        assertEquals(0, viewPager.currentItem)
    }
}

private const val ITEM_COUNT = 10
