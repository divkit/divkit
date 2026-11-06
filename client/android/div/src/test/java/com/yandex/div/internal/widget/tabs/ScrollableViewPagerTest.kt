package com.yandex.div.internal.widget.tabs

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ScrollableViewPagerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val viewPager = ScrollableViewPager(context)
    private val content = object : View(context) {
        var canScrollForward = false

        override fun canScrollHorizontally(direction: Int) = canScrollForward && direction == 1
    }

    @Test
    fun `disabled page prevents content swipes`() {
        viewPager.disabledScrollPages = setOf(0)

        assertFalse(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `enabling pages preserves global swipe restriction`() {
        viewPager.isScrollEnabled = false

        viewPager.disabledScrollPages = emptySet()

        assertFalse(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `large first move leaves scrolling child in control`() {
        content.canScrollForward = true

        dragLeftOverContent()

        assertFalse(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `large first move allows paging at child boundary`() {
        dragLeftOverContent()

        assertTrue(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `instant selection updates current item before attach`() {
        viewPager.adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn 2
        }

        viewPager.scrollToItem(1, 0)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `cancel discards queued selection`() {
        queueSelection()

        viewPager.cancelScroll()
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `owner cancels queued selection before adapter replacement`() {
        queueSelection()

        viewPager.cancelScroll()
        viewPager.adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn 2
        }
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `release discards queued selection before reuse`() {
        queueSelection()

        viewPager.release()
        viewPager.adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn 2
        }
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `touch up unlocks paging after scrolling child gesture`() {
        content.canScrollForward = true
        dragLeftOverContent()

        dispatchTouchEvent(MotionEvent.ACTION_UP)

        assertTrue(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `touch cancel preserves global swipe restriction`() {
        content.canScrollForward = true
        dragLeftOverContent()
        viewPager.isScrollEnabled = false

        dispatchTouchEvent(MotionEvent.ACTION_CANCEL)

        assertFalse(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `touch cancel unlocks paging after scrolling child gesture`() {
        content.canScrollForward = true
        dragLeftOverContent()

        dispatchTouchEvent(MotionEvent.ACTION_CANCEL)

        assertTrue(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `touch down discards queued selection`() {
        queueSelection()

        dispatchTouchEvent(MotionEvent.ACTION_DOWN)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `default duration updates selection before attach`() {
        viewPager.adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn 2
        }

        viewPager.scrollToItem(1, duration = null)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `initial selection stays idle before first layout`() {
        viewPager.adapter = PagesAdapter()

        viewPager.scrollToItem(2, duration = null)

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
    }

    @Test
    fun `selection stays idle after adapter replacement`() {
        viewPager.adapter = PagesAdapter()
        layoutPager()
        viewPager.adapter = PagesAdapter()

        viewPager.scrollToItem(2, duration = null)

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
    }

    @Test
    fun `first layout does not report a page selection`() {
        val listener = mock<ViewPager.OnPageChangeListener>()
        viewPager.adapter = PagesAdapter()
        viewPager.addOnPageChangeListener(listener)

        layoutPager()

        verify(listener, never()).onPageSelected(any())
    }

    @Test
    fun `changed selection is reported once`() {
        val listener = mock<ViewPager.OnPageChangeListener>()
        viewPager.adapter = PagesAdapter()
        layoutPager()
        viewPager.addOnPageChangeListener(listener)

        viewPager.scrollToItem(1, duration = 0)
        layoutPager()

        verify(listener).onPageSelected(1)
    }

    @Test
    fun `paging stays enabled when scrolling content appears during a gesture`() {
        dragLeftOverContent()
        content.canScrollForward = true

        dispatchTouchEvent(MotionEvent.ACTION_MOVE)

        assertTrue(viewPager.viewPager.isUserInputEnabled)
    }

    @Test
    fun `default jump of three pages stays animated`() {
        viewPager.adapter = PagesAdapter()
        layoutPager()

        viewPager.scrollToItem(3, duration = null)

        assertEquals(ViewPager2.SCROLL_STATE_SETTLING, viewPager.viewPager.scrollState)
    }

    @Test
    fun `default jump of four pages is instant`() {
        viewPager.adapter = PagesAdapter()
        layoutPager()

        viewPager.scrollToItem(4, duration = null)

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
        assertEquals(4, viewPager.currentItem)
    }

    @Test
    fun `default backward long jump is instant`() {
        viewPager.adapter = PagesAdapter()
        viewPager.scrollToItem(8, duration = 0)
        layoutPager()

        viewPager.scrollToItem(0, duration = null)

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `replacement default jump uses the actual scroll position`() {
        viewPager.adapter = PagesAdapter()
        layoutPager()
        viewPager.scrollToItem(3, duration = null)

        viewPager.scrollToItem(6, duration = null)
        layoutPager()

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
        assertEquals(6, viewPager.currentItem)
    }

    @Test
    fun `replacement default jump uses the fractional RTL scroll position`() {
        viewPager.layoutDirection = View.LAYOUT_DIRECTION_RTL
        viewPager.adapter = PagesAdapter()
        layoutPager()
        viewPager.scrollToItem(3, duration = null)
        requireNotNull(viewPager.getRecyclerView()).scrollBy(-100, 0)

        viewPager.scrollToItem(4, duration = null)
        layoutPager()

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
        assertEquals(4, viewPager.currentItem)
    }

    @Test
    fun `cancel prevents a default long jump from scrolling a replacement adapter`() = withAttachedPager {
        viewPager.adapter = PagesAdapter()
        layoutPager()
        viewPager.scrollToItem(8, duration = null)

        viewPager.cancelScroll()
        viewPager.adapter = PagesAdapter()
        layoutPager()
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `queued default long jump is instant when executed`() = withAttachedPager {
        viewPager.adapter = PagesAdapter()
        layoutPager()
        val looper = shadowOf(Looper.getMainLooper())
        looper.idle()
        viewPager.scrollToItem(1, duration = 200)

        viewPager.scrollToItem(8, duration = null)
        looper.runOneTask()

        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.viewPager.scrollState)
        assertEquals(8, viewPager.currentItem)
    }

    private fun withAttachedPager(block: () -> Unit) {
        val activity = Robolectric.buildActivity(Activity::class.java).setup()
        try {
            activity.get().setContentView(viewPager)
            block()
        } finally {
            activity.pause().stop().destroy()
        }
    }

    private fun layoutPager() {
        viewPager.layoutParams = FrameLayout.LayoutParams(200, 200)
        val size = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY)
        viewPager.measure(size, size)
        viewPager.layout(0, 0, 200, 200)
    }

    private class PagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = 10

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val page = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            return object : RecyclerView.ViewHolder(page) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }

    private fun queueSelection() {
        viewPager.adapter = mock<RecyclerView.Adapter<*>> {
            on { itemCount } doReturn 2
        }
        viewPager.scrollToItem(1, 200)
    }

    private fun dispatchTouchEvent(action: Int) {
        val event = MotionEvent.obtain(0, 32, action, 50f, 50f, 0)
        try {
            viewPager.dispatchTouchEvent(event)
        } finally {
            event.recycle()
        }
    }

    private fun dragLeftOverContent() {
        requireNotNull(viewPager.getRecyclerView()).addView(content)
        content.layout(0, 0, 200, 200)
        val down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 150f, 50f, 0)
        val move = MotionEvent.obtain(0, 16, MotionEvent.ACTION_MOVE, 50f, 50f, 0)
        try {
            viewPager.dispatchTouchEvent(down)
            viewPager.dispatchTouchEvent(move)
        } finally {
            down.recycle()
            move.recycle()
        }
    }
}
