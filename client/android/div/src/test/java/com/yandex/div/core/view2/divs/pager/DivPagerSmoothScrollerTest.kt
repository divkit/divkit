package com.yandex.div.core.view2.divs.pager

import android.app.Activity
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.items.DivViewWithItems
import com.yandex.div.core.view2.items.DivViewWithItemsController
import com.yandex.div.core.view2.items.ScrollDirection
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer
import java.time.Duration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DivPagerSmoothScrollerTest {

    private val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    private val activity = activityController.get()
    private val view = DivPagerView(activity)
    private val viewPager = view.viewPager
    private val scroller = DivPagerSmoothScroller(viewPager, requireNotNull(view.getRecyclerView()))
    private val pager = DivViewWithItems.Pager(view)

    @BeforeTest
    fun setUp() {
        ShadowChoreographer.setPaused(true)
        viewPager.adapter = PageAdapter()
        view.divPagerSmoothScroller = scroller
        activity.setContentView(view)
        advanceTime(32)
        layout()
    }

    @AfterTest
    fun tearDown() {
        scroller.dispose()
        activityController.pause().stop().destroy()
    }

    @Test
    fun `duration scroll reaches requested page`() {
        pager.setCurrentItem(2, animated = true, duration = 300)

        advanceTime(350)

        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `step scroll starts fake drag through pager controller`() {
        pager.setCurrentItem(pager.getNearestItem(ScrollDirection.NEXT), animated = true, duration = 300)

        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)
    }

    @Test
    fun `scroll without duration uses native pager animation`() {
        pager.setCurrentItem(6, animated = true)
        advanceTime(32)

        assertFalse(viewPager.isFakeDragging)
        assertEquals(ViewPager2.SCROLL_STATE_SETTLING, viewPager.scrollState)
        assertEquals(6, viewPager.currentItem)
    }

    @Test
    fun `sequential instant relative actions advance two pages`() {
        val controller = DivViewWithItemsController(pager, animationsEnabled = true)

        controller.changeCurrentItemByStep(overflow = null, step = 1, animated = false)
        controller.changeCurrentItemByStep(overflow = null, step = 1, animated = false)
        advanceTime(32)

        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `sequential zero duration relative actions advance two pages`() {
        val controller = DivViewWithItemsController(pager, animationsEnabled = true)

        controller.changeCurrentItemByStep(overflow = null, step = 1, animated = true, duration = 0)
        controller.changeCurrentItemByStep(overflow = null, step = 1, animated = true, duration = 0)
        advanceTime(32)

        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `zero duration selects page without fake drag`() {
        pager.setCurrentItem(2, animated = true, duration = 0)

        advanceTime(32)

        assertFalse(viewPager.isFakeDragging)
        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `scroller default duration selects page without fake drag`() {
        scroller.scrollTo(2)

        advanceTime(32)

        assertFalse(viewPager.isFakeDragging)
        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `instant scroll ignores duration`() {
        pager.setCurrentItem(2, animated = false, duration = 300)
        advanceTime(32)

        assertFalse(viewPager.isFakeDragging)
        assertEquals(2, viewPager.currentItem)
    }

    @Test
    fun `touch cancels duration scroll`() {
        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)

        view.dispatchTouchEvent(event)
        event.recycle()

        assertFalse(viewPager.isFakeDragging)
    }

    @Test
    fun `detach cancels duration scroll`() {
        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)

        activity.setContentView(View(activity))

        assertFalse(viewPager.isFakeDragging)
    }

    @Test
    fun `dispose cancels duration scroll`() {
        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)

        scroller.dispose()

        assertFalse(viewPager.isFakeDragging)
    }

    @Test
    fun `virtual range change cancels duration scroll`() {
        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)

        scroller.updateVirtualItemCount(2)

        assertFalse(viewPager.isFakeDragging)
    }

    @Test
    fun `unchanged virtual range keeps duration scroll running`() {
        pager.setCurrentItem(2, animated = true, duration = 300)

        scroller.updateVirtualItemCount(0)

        advanceTime(32)
        assertTrue(viewPager.isFakeDragging)
    }

    @Test
    fun `infinite duration scroll finishes in central copy`() {
        // Arrange: three real pages and two virtual pages at each edge.
        scroller.updateVirtualItemCount(2)
        viewPager.setCurrentItem(2, false)
        layout()

        // Act
        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(350)
        layout()

        // Assert
        assertEquals(3, viewPager.currentItem)
    }

    @Test
    fun `ordinary duration scroll keeps final page without normalization`() {
        pager.setCurrentItem(6, animated = true, duration = 300)

        advanceTime(350)

        assertEquals(6, viewPager.currentItem)
    }

    @Test
    fun `duration scroll waits for UI queue`() {
        pager.setCurrentItem(2, animated = true, duration = 300)

        assertFalse(viewPager.isFakeDragging)
    }

    @Test
    fun `instant scroll waits for UI queue`() {
        pager.setCurrentItem(2, animated = false, duration = 300)

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `native scroll waits for UI queue`() {
        pager.setCurrentItem(2, animated = true)

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `long duration scroll remains active before requested duration`() {
        pager.setCurrentItem(2, animated = true, duration = 600)

        advanceTime(350)

        assertTrue(viewPager.isFakeDragging)
    }

    @Test
    fun `dragging callback can cancel duration scroll`() {
        onScrollState(ViewPager2.SCROLL_STATE_DRAGGING) { scroller.cancel() }

        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(350)

        assertFalse(viewPager.isFakeDragging)
        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `dragging callback can replace duration scroll`() {
        onScrollState(ViewPager2.SCROLL_STATE_DRAGGING) {
            pager.setCurrentItem(1, animated = true, duration = 300)
        }

        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(350)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `last queued request determines final page`() {
        pager.setCurrentItem(6, animated = true, duration = 300)
        pager.setCurrentItem(1, animated = true, duration = 300)

        advanceTime(350)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `cancel discards queued request`() {
        pager.setCurrentItem(2, animated = true, duration = 300)

        scroller.cancel()
        advanceTime(350)

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `dispose discards queued request`() {
        pager.setCurrentItem(2, animated = true, duration = 300)

        scroller.dispose()
        advanceTime(350)

        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun `new duration scroll replaces running transition`() {
        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(80)

        pager.setCurrentItem(1, animated = true, duration = 300)
        advanceTime(350)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `completion callback can request duration scroll`() {
        pager.setCurrentItem(2, animated = true, duration = 300)
        advanceTime(32)
        onScrollState(ViewPager2.SCROLL_STATE_IDLE) { pager.setCurrentItem(1, animated = true, duration = 300) }

        advanceTime(700)

        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun `cancellation callback replacement does not supersede selected request`() {
        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(80)
        onScrollState(ViewPager2.SCROLL_STATE_SETTLING) { pager.setCurrentItem(1, animated = true, duration = 300) }

        pager.setCurrentItem(4, animated = true, duration = 300)
        advanceTime(350)

        assertEquals(4, viewPager.currentItem)
    }

    @Test
    fun `cancellation callback cannot discard selected request`() {
        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(80)
        onScrollState(ViewPager2.SCROLL_STATE_SETTLING) { scroller.cancel() }

        pager.setCurrentItem(4, animated = true, duration = 300)
        advanceTime(350)

        assertFalse(viewPager.isFakeDragging)
        assertFalse(scroller.isRunning)
        assertEquals(4, viewPager.currentItem)
    }

    @Test
    fun `repeated cancel from callback ends fake drag`() {
        pager.setCurrentItem(6, animated = true, duration = 300)
        advanceTime(80)
        onScrollState(ViewPager2.SCROLL_STATE_SETTLING) { scroller.cancel() }

        scroller.cancel()

        assertFalse(viewPager.isFakeDragging)
        assertFalse(scroller.isRunning)
    }

    private fun onScrollState(expectedState: Int, action: () -> Unit) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var handled = false

            override fun onPageScrollStateChanged(state: Int) {
                if (state == expectedState && !handled) {
                    handled = true
                    action()
                }
            }
        })
    }

    private fun advanceTime(millis: Long) {
        val mainLooper = shadowOf(Looper.getMainLooper())
        repeat((millis / 16).toInt()) {
            mainLooper.idleFor(Duration.ofMillis(16))
        }
        mainLooper.idleFor(Duration.ofMillis(millis % 16))
    }

    private fun layout() {
        val size = View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
        view.measure(size, size)
        view.layout(0, 0, 400, 400)
    }

    private class PageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = 7

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val page = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            return object : RecyclerView.ViewHolder(page) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
}
