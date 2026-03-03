package com.yandex.div.core.view2.items

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowView

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [PagerItemsViewTest.ShadowViewPager2::class])
class PagerItemsViewTest {

    private val adapter = mock<RecyclerView.Adapter<*>> {
        on { itemCount } doReturn ITEM_COUNT
    }
    private val viewPager = ViewPager2(ApplicationProvider.getApplicationContext()).apply {
        adapter = this@PagerItemsViewTest.adapter
    }
    private val view = mock<DivPagerView> {
        on { viewPager } doReturn viewPager
        on { resources } doReturn mock()
    }

    private val underTest = DivViewWithItems.Pager(view)

    @Test
    fun `item count`() {
        Assert.assertEquals(ITEM_COUNT, underTest.itemCount)
    }

    @Test
    fun `get current item`() {
        viewPager.shadow().currentItem = 5

        Assert.assertEquals(5, underTest.currentItem)
    }

    @Test
    fun `set current item`() {
        underTest.currentItem = 5

        Assert.assertEquals(5, viewPager.shadow().currentItem)
    }

    @Test
    fun `set current item out of range`() = disableAssertions {
        underTest.currentItem = -5
        underTest.currentItem = ITEM_COUNT

        Assert.assertEquals(-1, viewPager.shadow().currentItem)
    }

    private fun ViewPager2.shadow(): ShadowViewPager2 {
        return Shadow.extract(this) as ShadowViewPager2
    }

    @Implements(ViewPager2::class)
    class ShadowViewPager2 : ShadowView() {

        @get:Implementation
        var currentItem: Int = -1

        @Implementation
        fun setCurrentItem(item: Int, smoothScroll: Boolean) {
            currentItem = item
        }
    }

    private companion object {
        private const val ITEM_COUNT = 10
    }
}
