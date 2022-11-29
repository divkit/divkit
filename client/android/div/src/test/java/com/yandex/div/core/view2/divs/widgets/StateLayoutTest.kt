package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.view.MotionEventBuilder
import com.yandex.div.internal.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [21])
@RunWith(RobolectricTestRunner::class)
class StateLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
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

        verify(underTest.swipeOutCallback)!!.invoke()
    }

    @Test
    fun `swipe out callback is triggered when swiping out to right`() {
        underTest.swipeOutCallback = mock<SwipeOutCallback>()
        underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 100, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 400, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 900, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_UP, 900, 100))

        verify(underTest.swipeOutCallback)!!.invoke()
    }

    @Test
    fun `short swipe doesn't perform swipe out`() {
        underTest.swipeOutCallback = mock<SwipeOutCallback>()
        underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 900, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 500, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_MOVE, 800, 100))
        underTest.onTouchEvent(event(MotionEvent.ACTION_UP, 800, 100))

        verify(underTest.swipeOutCallback, never())!!.invoke()
    }

    @Test
    fun `event is not intercepted if no swipe out listener is set`() {
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_DOWN, 100, 100)))
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_MOVE, 200, 200)))
        Assert.assertFalse(underTest.onInterceptTouchEvent(event(MotionEvent.ACTION_UP, 200, 200)))
    }

    private fun event(action: Int, x: Int, y: Int) = MotionEventBuilder.newBuilder()
        .setAction(action)
        .setPointer(x.toFloat(), y.toFloat())
        .build()
}

private interface SwipeOutCallback: () -> Unit
