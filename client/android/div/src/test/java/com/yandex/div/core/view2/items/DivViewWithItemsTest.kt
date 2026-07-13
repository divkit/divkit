package com.yandex.div.core.view2.items

import android.content.res.Resources
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class DivViewWithItemsTest {

    private val resources = mock<Resources> {
        on { displayMetrics } doReturn mock()
    }

    @Test
    fun `create gallery`() {
        val view = mock<DivRecyclerView> {
            on { resources } doReturn resources
        }
        assertThat(
            DivViewWithItems.create(view),
            instanceOf(DivViewWithItems.Gallery::class.java)
        )
    }

    @Test
    fun `create pager`() {
        val view = mock<DivPagerView> {
            on { resources } doReturn resources
        }
        assertThat(
            DivViewWithItems.create(view),
            instanceOf(DivViewWithItems.Pager::class.java)
        )
    }

    @Test
    fun `create tabs`() {
        val view = mock<DivTabsLayout> {
            on { resources } doReturn resources
        }
        assertThat(
            DivViewWithItems.create(view),
            instanceOf(DivViewWithItems.Tabs::class.java)
        )
    }

    @Test
    fun `cannot create`() {
        assertNull(DivViewWithItems.create(mock<DivScrollActionHolder>()))
    }
}
