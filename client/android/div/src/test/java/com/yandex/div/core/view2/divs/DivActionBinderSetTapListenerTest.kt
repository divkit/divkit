package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.core.view2.DivGestureListener
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DivActionBinderSetTapListenerTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun `onSingleTapUp invokes singleTapListener when no doubleTapListener set`() {
        val gestureListener = DivGestureListener(awaitLongClick = false)
        var invokeCount = 0
        gestureListener.onSingleTapListener = { invokeCount++ }

        gestureListener.onSingleTapUp(mockMotionEvent())

        assertEquals(1, invokeCount)
    }

    @Test
    fun `onSingleTapUp does not invoke singleTapListener when doubleTapListener is set`() {
        val gestureListener = DivGestureListener(awaitLongClick = false)
        var invokeCount = 0
        gestureListener.onSingleTapListener = { invokeCount++ }
        gestureListener.onDoubleTapListener = { /* no-op */ }

        gestureListener.onSingleTapUp(mockMotionEvent())

        assertEquals(0, invokeCount)
    }

    @Test
    fun `onSingleTapConfirmed invokes singleTapListener when doubleTapListener is set`() {
        val gestureListener = DivGestureListener(awaitLongClick = false)
        var invokeCount = 0
        gestureListener.onSingleTapListener = { invokeCount++ }
        gestureListener.onDoubleTapListener = { /* no-op */ }

        gestureListener.onSingleTapConfirmed(mockMotionEvent())

        assertEquals(1, invokeCount)
    }

    @Test
    fun `onSingleTapConfirmed does not invoke singleTapListener when doubleTapListener is null`() {
        val gestureListener = DivGestureListener(awaitLongClick = false)
        var invokeCount = 0
        gestureListener.onSingleTapListener = { invokeCount++ }

        gestureListener.onSingleTapConfirmed(mockMotionEvent())

        assertEquals(0, invokeCount)
    }

    @Test
    fun `view reuse from no-doubletap to doubletap clears onClickListener`() {
        val view = View(context)
        val gestureListenerFirst = DivGestureListener(awaitLongClick = false)

        var clickCountFirst = 0
        view.setOnClickListener { clickCountFirst++ }
        gestureListenerFirst.onSingleTapListener = null

        val gestureListenerSecond = DivGestureListener(awaitLongClick = false)
        gestureListenerSecond.onDoubleTapListener = { /* doubletap handler */ }

        var clickCountSecond = 0
        view.setOnClickListener(null)
        gestureListenerSecond.onSingleTapListener = { clickCountSecond++ }

        view.performClick()

        assertEquals("Old onClickListener must not fire after re-bind with doubletap", 0, clickCountFirst)
        assertEquals("New listener must not have been invoked via performClick", 0, clickCountSecond)
        assertNotNull(gestureListenerSecond.onSingleTapListener)
    }

    @Test
    fun `view reuse from doubletap to no-doubletap clears gestureListener onSingleTapListener`() {
        val view = View(context)

        val gestureListenerFirst = DivGestureListener(awaitLongClick = false)
        gestureListenerFirst.onDoubleTapListener = { /* doubletap handler */ }
        var clickCountFirst = 0
        gestureListenerFirst.onSingleTapListener = { clickCountFirst++ }

        gestureListenerFirst.onSingleTapListener = null
        var clickCountSecond = 0
        view.setOnClickListener { clickCountSecond++ }

        assertNull(
            "Stale gestureListener.onSingleTapListener must be cleared after re-bind without doubletap",
            gestureListenerFirst.onSingleTapListener
        )

        view.performClick()
        assertEquals("New onClickListener must fire exactly once", 1, clickCountSecond)
        assertEquals("Old gestureListener.onSingleTapListener must not fire after being cleared", 0, clickCountFirst)
    }

    @Test
    fun `gestureListener singleTapListener fires exactly once via onSingleTapConfirmed with doubletap set`() {
        val gestureListener = DivGestureListener(awaitLongClick = false)
        gestureListener.onDoubleTapListener = { /* doubletap */ }

        var invokeCount = 0
        gestureListener.onSingleTapListener = { invokeCount++ }

        gestureListener.onSingleTapUp(mockMotionEvent())
        gestureListener.onSingleTapConfirmed(mockMotionEvent())

        assertEquals("Listener must be called exactly once via onSingleTapConfirmed", 1, invokeCount)
    }

    private fun mockMotionEvent(): android.view.MotionEvent {
        return android.view.MotionEvent.obtain(0L, 0L, android.view.MotionEvent.ACTION_UP, 0f, 0f, 0)
    }
}
