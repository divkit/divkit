package com.yandex.div.core.view2.divs.widgets

import android.os.Looper.getMainLooper
import android.view.MotionEvent
import android.view.View
import androidx.test.core.view.MotionEventBuilder
import com.yandex.div.test.testContextThemeWrapper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class StateLayoutTest {

    private val context = testContextThemeWrapper()

    private val view = View(context).apply {
        left = 0
        right  = 1000
        top = 0
        bottom = 1000
    }
    
    private val underTest = DivStateLayout(context).apply {
        addView(view)
    }

    @Test
    fun `swipe out callback is triggered when swiping out to left`() {
        underTest.swipeOutCallback = mock<SwipeOutCallback>()
        underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 900, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 400, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 300, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_UP, 300, 100))

        drainMainLoop()

        verify(underTest.swipeOutCallback)!!.invoke()
    }

    @Test
    fun `swipe out callback is triggered when swiping out to right`() {
        underTest.swipeOutCallback = mock<SwipeOutCallback>()
        underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 100, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 400, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 900, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_UP, 900, 100))

        drainMainLoop()

        verify(underTest.swipeOutCallback)!!.invoke()
    }

    @Test
    fun `short swipe doesn't perform swipe out`() {
        underTest.swipeOutCallback = mock<SwipeOutCallback>()
        underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 900, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 500, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 800, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_UP, 800, 100))

        drainMainLoop()

        verify(underTest.swipeOutCallback, never())!!.invoke()
    }

    @Test
    fun `fast short swipe to right performs swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        swipe(100 to 0, 200 to 10, 300 to 20, 300 to 30)

        verify(callback).invoke()
    }

    @Test
    fun `fast short swipe to left performs swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        swipe(900 to 0, 800 to 10, 700 to 20, 700 to 30)

        verify(callback).invoke()
    }

    @Test
    fun `short swipe below fling velocity doesn't perform swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        swipe(100 to 0, 200 to 500, 201 to 510, 202 to 520, 203 to 530, 204 to 540, 205 to 550)

        verify(callback, never()).invoke()
    }

    @Test
    fun `fling against swipe direction doesn't perform swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        swipe(100 to 0, 300 to 500, 250 to 510, 200 to 520, 200 to 530)

        verify(callback, never()).invoke()
    }

    @Test
    fun `vertical fling doesn't perform swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        touch(MotionEvent.ACTION_DOWN, 100, 100, time = 0)
        touch(MotionEvent.ACTION_MOVE, 300, 100, time = 500)
        touch(MotionEvent.ACTION_MOVE, 310, 300, time = 510)
        touch(MotionEvent.ACTION_MOVE, 320, 500, time = 520)
        touch(MotionEvent.ACTION_UP, 320, 500, time = 530)
        drainMainLoop()

        verify(callback, never()).invoke()
    }

    @Test
    fun `fast swipe right after previous swipe performs swipe out`() {
        val callback = mock<SwipeOutCallback>()
        underTest.swipeOutCallback = callback

        dispatchSwipe(100 to 0, 200 to 500, 201 to 510, 202 to 520, 203 to 530, 203 to 540)
        verify(callback, never()).invoke()

        dispatchSwipe(100 to 640, 200 to 650, 300 to 660, 300 to 670)
        verify(callback).invoke()
    }

    @Test
    fun `event is not intercepted if no swipe out listener is set`() {
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 100, 100)))
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_MOVE, 200, 200)))
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_UP, 200, 200)))
    }

    private fun event(action: Int, x: Int, y: Int, time: Long = 0, downTime: Long = 0) = MotionEventBuilder.newBuilder()
        .setDownTime(downTime)
        .setEventTime(time)
        .setAction(action)
        .setPointer(x.toFloat(), y.toFloat())
        .build()

    private fun swipe(vararg points: Pair<Int, Int>) {
        points.forEachIndexed { index, (x, time) ->
            val action = when (index) {
                0 -> MotionEvent.ACTION_DOWN
                points.lastIndex -> MotionEvent.ACTION_UP
                else -> MotionEvent.ACTION_MOVE
            }
            touch(action, x, 100, time)
        }
        drainMainLoop()
    }

    private fun dispatchSwipe(vararg points: Pair<Int, Int>) {
        val downTime = points.first().second.toLong()
        points.forEachIndexed { index, (x, time) ->
            val action = when (index) {
                0 -> MotionEvent.ACTION_DOWN
                points.lastIndex -> MotionEvent.ACTION_UP
                else -> MotionEvent.ACTION_MOVE
            }
            underTest.dispatchTouchEvent(event(action, x, 100, time.toLong(), downTime))
        }
        drainMainLoop()
    }

    private fun touch(action: Int, x: Int, y: Int, time: Int) {
        val event = event(action, x, y, time.toLong())
        if (action == MotionEvent.ACTION_DOWN) {
            underTest.onInterceptTouchEvent(event)
        } else {
            underTest.onTouchEvent(event)
        }
    }

    private fun drainMainLoop() {
        shadowOf(getMainLooper()).idle()
    }
}

private interface SwipeOutCallback: () -> Unit
