package com.yandex.div.core.view2.items

import android.widget.FrameLayout
import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class DivViewWithItemsTest {

    @Test
    fun `create paging gallery`() {
        val view = mock<DivRecyclerView> {
            on { div } doReturn createDivGallery(DivGallery.ScrollMode.PAGING)
            on { resources } doReturn mock()
        }
        assertThat(
            DivViewWithItems.create(
                view,
                mock()
            ) { Direction.NEXT },
            instanceOf(DivViewWithItems.PagingGallery::class.java)
        )
    }

    @Test
    fun `create gallery`() {
        val view = mock<DivRecyclerView> {
            on { div } doReturn createDivGallery(DivGallery.ScrollMode.DEFAULT)
            on { resources } doReturn mock()
        }
        assertThat(
            DivViewWithItems.create(
                view,
                mock()
            ) { Direction.NEXT },
            instanceOf(DivViewWithItems.Gallery::class.java)
        )
    }

    @Test
    fun `create pager`() {
        val view = mock<DivPagerView> {
            on { resources } doReturn mock()
        }
        assertThat(
            DivViewWithItems.create(view, mock()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Pager::class.java)
        )
    }

    @Test
    fun `create tabs`() {
        val view = mock<DivTabsLayout> {
            on { resources } doReturn mock()
        }
        assertThat(
            DivViewWithItems.create(view, mock()) { Direction.NEXT },
            instanceOf(DivViewWithItems.Tabs::class.java)
        )
    }

    @Test
    fun `cannot create`() {
        assertNull(DivViewWithItems.create(mock<FrameLayout>(), mock()) { Direction.NEXT })
    }

    private fun createDivGallery(scrollMode: DivGallery.ScrollMode) =
        Div.Gallery(DivGallery(scrollMode = scrollMode.asExpression(), items = emptyList()))
}
