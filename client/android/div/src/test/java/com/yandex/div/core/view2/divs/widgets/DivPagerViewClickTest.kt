package com.yandex.div.core.view2.divs.widgets

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DivPagerViewClickTest {

    private val context = RuntimeEnvironment.application
    private lateinit var pagerView: DivPagerView
    private lateinit var parent: FrameLayout
    private lateinit var root: FrameLayout

    @Before
    fun setUp() {
        pagerView = DivPagerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        parent = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            addView(pagerView)
        }
        root = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        root.addView(parent)

        root.measure(
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
        )
        root.layout(0, 0, 500, 500)
    }

    @Test
    fun `tap on clickable pager triggers pager click`() {
        var pagerClicked = false
        pagerView.setOnClickListener { pagerClicked = true }

        var parentClicked = false
        parent.setOnClickListener { parentClicked = true }

        simulateTap(pagerView)

        assertTrue(pagerClicked)
        assertFalse(parentClicked)
    }

    @Test
    fun `tap on non-clickable pager propagates to clickable parent`() {
        var parentClicked = false
        parent.setOnClickListener { parentClicked = true }

        simulateTap(pagerView)

        assertTrue(parentClicked)
    }

    @Test
    fun `long press on long-clickable pager triggers pager long click`() {
        var pagerLongClicked = false
        pagerView.setOnLongClickListener { pagerLongClicked = true; true }

        var parentLongClicked = false
        parent.setOnLongClickListener { parentLongClicked = true; true }

        simulateLongPress(pagerView)

        assertTrue(pagerLongClicked)
        assertFalse(parentLongClicked)
    }

    @Test
    fun `long press on non-long-clickable pager propagates to long-clickable parent`() {
        var parentLongClicked = false
        parent.setOnLongClickListener { parentLongClicked = true; true }

        simulateLongPress(pagerView)

        assertTrue(parentLongClicked)
    }

    private fun simulateTap(target: DivPagerView) {
        val downTime = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 50f, 50f, 0)
        val up = MotionEvent.obtain(downTime, downTime + 50, MotionEvent.ACTION_UP, 50f, 50f, 0)
        target.dispatchTouchEvent(down)
        target.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
    }

    private fun simulateLongPress(target: DivPagerView) {
        val downTime = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 50f, 50f, 0)
        target.dispatchTouchEvent(down)
        down.recycle()
        // GestureDetector fires onLongPress after a timeout; Robolectric uses
        // the main looper, so we advance the clock past the long-press threshold.
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
    }
}
