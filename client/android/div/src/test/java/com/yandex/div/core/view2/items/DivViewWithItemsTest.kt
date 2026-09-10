package com.yandex.div.core.view2.items

import android.content.res.Resources
import androidx.recyclerview.widget.DivLinearLayoutManager
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div2.DivGallery.ScrollMode
import com.yandex.div2.DivSizeUnit
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DivViewWithItemsTest {

    private val layoutManager = mock<DivLinearLayoutManager> {
        on { isHorizontal } doReturn true
    }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn mock()
    }
    private val recyclerView = mock<DivRecyclerView> {
        on { resources } doReturn resources
        on { layoutManager } doReturn layoutManager
        on { scrollMode } doReturn ScrollMode.DEFAULT
        on { computeHorizontalScrollOffset() } doReturn CURRENT_OFFSET
        on { computeHorizontalScrollRange() } doReturn SCROLL_RANGE
        on { width } doReturn VIEW_WIDTH
    }
    private val gallery = DivViewWithItems.Gallery(recyclerView)

    @Test
    fun `create gallery`() {
        assertIs<DivViewWithItems.Gallery>(DivViewWithItems.create(recyclerView))
    }

    @Test
    fun `create pager`() {
        val view = mock<DivPagerView> {
            on { resources } doReturn resources
        }
        assertIs<DivViewWithItems.Pager>(DivViewWithItems.create(view))
    }

    @Test
    fun `create tabs`() {
        val view = mock<DivTabsLayout> {
            on { resources } doReturn resources
        }
        assertIs<DivViewWithItems.Tabs>(DivViewWithItems.create(view))
    }

    @Test
    fun `cannot create`() {
        assertNull(DivViewWithItems.create(mock<DivScrollActionHolder>()))
    }

    @Test
    fun `scrolls to offset without animation in default mode`() {
        gallery.scrollTo(TARGET_POSITION, animated = false)

        verify(recyclerView).scrollBy(TARGET_POSITION - CURRENT_OFFSET, 0)
    }

    @Test
    fun `scrolls to offset with animation in default mode`() {
        gallery.scrollTo(BACKWARD_TARGET_POSITION, animated = true)

        verify(recyclerView).smoothScrollBy(BACKWARD_TARGET_POSITION - CURRENT_OFFSET, 0)
    }

    @Test
    fun `does not scroll to offset without animation in paging mode`() {
        whenever(recyclerView.scrollMode).thenReturn(ScrollMode.PAGING)

        gallery.scrollTo(TARGET_POSITION, animated = false)

        verify(recyclerView, never()).scrollBy(any(), any())
    }

    @Test
    fun `does not scroll to offset with animation in paging mode`() {
        whenever(recyclerView.scrollMode).thenReturn(ScrollMode.PAGING)

        gallery.scrollTo(BACKWARD_TARGET_POSITION, animated = true)

        verify(recyclerView, never()).smoothScrollBy(any(), any())
    }

    @Test
    fun `does not scroll to dp offset in paging mode`() {
        whenever(recyclerView.scrollMode).thenReturn(ScrollMode.PAGING)

        gallery.scrollTo(TARGET_POSITION, animated = false, DivSizeUnit.DP)

        verify(recyclerView, never()).scrollBy(any(), any())
    }

    @Test
    fun `scrolls to end in paging mode`() {
        whenever(recyclerView.scrollMode).thenReturn(ScrollMode.PAGING)

        gallery.scrollToTheEnd(animated = false)

        verify(recyclerView).scrollBy(SCROLL_RANGE - VIEW_WIDTH - CURRENT_OFFSET, 0)
    }

    private companion object {
        private const val TARGET_POSITION = 100
        private const val BACKWARD_TARGET_POSITION = 0
        private const val CURRENT_OFFSET = 50
        private const val SCROLL_RANGE = 1000
        private const val VIEW_WIDTH = 200
    }
}
