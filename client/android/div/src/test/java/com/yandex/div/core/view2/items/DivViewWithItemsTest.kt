package com.yandex.div.core.view2.items

import android.widget.FrameLayout
import com.yandex.div.core.view.layout.TabsLayout
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSnappyRecyclerView
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock

class DivViewWithItemsTest {

    @Test
    fun `create paging gallery`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<DivSnappyRecyclerView>()) { Direction.NEXT },
            instanceOf(DivViewWithItems.PagingGallery::class.java)
        )
    }

    @Test
    fun `create gallery`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<DivRecyclerView>()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Gallery::class.java)
        )
    }

    @Test
    fun `create pager`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<DivPagerView>()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Pager::class.java)
        )
    }

    @Test
    fun `create tabs`() {
        Assert.assertThat(
            DivViewWithItems.create(mock<TabsLayout>()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Tabs::class.java)
        )
    }

    @Test
    fun `cannot create`() {
        Assert.assertNull(DivViewWithItems.create(mock<FrameLayout>()) { Direction.NEXT })
    }
}
