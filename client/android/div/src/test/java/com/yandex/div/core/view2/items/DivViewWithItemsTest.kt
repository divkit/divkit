package com.yandex.div.core.view2.items

import android.widget.FrameLayout
import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div2.DivGallery
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class DivViewWithItemsTest {

    @Test
    fun `create paging gallery`() {
        Assert.assertThat(
            DivViewWithItems.create(
                mock<DivRecyclerView> { on { div } doReturn createDivGallery(DivGallery.ScrollMode.PAGING) },
                mock()
            ) { Direction.NEXT },
            instanceOf(DivViewWithItems.PagingGallery::class.java)
        )
    }

    @Test
    fun `create gallery`() {
        Assert.assertThat(
            DivViewWithItems.create(
                mock<DivRecyclerView> { on { div } doReturn createDivGallery(DivGallery.ScrollMode.DEFAULT) },
                mock()
            ) { Direction.NEXT },
            instanceOf(DivViewWithItems.Gallery::class.java)
        )
    }

    @Test
    fun `create pager`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<DivPagerView>(), mock()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Pager::class.java)
        )
    }

    @Test
    fun `create tabs`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<DivTabsLayout>(), mock()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Tabs::class.java)
        )
    }

    @Test
    fun `cannot create`() {
        Assert.assertNull(DivViewWithItems.create(mock<FrameLayout>(), mock()) { Direction.NEXT })
    }

    private fun createDivGallery(scrollMode: DivGallery.ScrollMode): DivGallery =
        DivGallery(scrollMode = scrollMode.asExpression(), items = emptyList())
}
