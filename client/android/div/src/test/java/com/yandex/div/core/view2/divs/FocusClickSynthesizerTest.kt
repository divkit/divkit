package com.yandex.div.core.view2.divs

import android.view.MotionEvent
import android.view.View
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Tests for [FocusClickSynthesizer].
 */
@RunWith(RobolectricTestRunner::class)
class FocusClickSynthesizerTest {

    private val touchSlop = 8
    private val longPressTimeout = 500L
    private val underTest = FocusClickSynthesizer(touchSlop, longPressTimeout)

    private var focused = false
    private val view = mock<View> {
        on { isFocusable } doReturn true
        on { isFocusableInTouchMode } doReturn true
        on { isClickable } doReturn true
        on { isLongClickable } doReturn false
        on { isFocused } doAnswer { focused }
        on { post(any()) } doAnswer { invocation ->
            focused = true
            (invocation.arguments[0] as Runnable).run()
            true
        }
    }

    @Test
    fun `create returns null when view is not focusable in touch mode`() {
        val target = mock<View> {
            on { isFocusableInTouchMode } doReturn false
            on { isClickable } doReturn true
        }

        assertNull(FocusClickSynthesizer.create(target))
    }

    @Test
    fun `create returns null when view is not clickable`() {
        val target = mock<View> {
            on { isFocusableInTouchMode } doReturn true
            on { isClickable } doReturn false
        }

        assertNull(FocusClickSynthesizer.create(target))
    }

    @Test
    fun `create returns synthesizer when view is focusable in touch mode and clickable`() {
        val target = View(RuntimeEnvironment.getApplication()).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
        }

        assertNotNull(FocusClickSynthesizer.create(target))
    }

    @Test
    fun `synthesizes click on tap that takes focus`() {
        dispatchTap(downTime = 0L, upTime = 50L)

        verify(view).performClick()
    }

    @Test
    fun `does not synthesize click when view was already focused on down`() {
        focused = true

        dispatchTap(downTime = 0L, upTime = 50L)

        verify(view, never()).performClick()
    }

    @Test
    fun `does not synthesize click when gesture moves beyond touch slop`() {
        underTest(view, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, 0L))
        underTest(view, obtain(MotionEvent.ACTION_MOVE, touchSlop + 1f, 0f, 10L))
        underTest(view, obtain(MotionEvent.ACTION_UP, touchSlop + 1f, 0f, 50L))

        verify(view, never()).performClick()
    }

    @Test
    fun `synthesizes click after long hold when view is not long clickable`() {
        // Platform clicks on UP after any hold if the view is not longClickable.
        dispatchTap(downTime = 0L, upTime = longPressTimeout)

        verify(view).performClick()
    }

    @Test
    fun `does not synthesize click after long press timeout when view is long clickable`() {
        val longClickableView = mock<View> {
            on { isFocusable } doReturn true
            on { isFocusableInTouchMode } doReturn true
            on { isClickable } doReturn true
            on { isLongClickable } doReturn true
            on { isFocused } doAnswer { focused }
            on { post(any()) } doAnswer { invocation ->
                focused = true
                (invocation.arguments[0] as Runnable).run()
                true
            }
        }

        underTest(longClickableView, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, 0L))
        underTest(longClickableView, obtain(MotionEvent.ACTION_UP, 0f, 0f, longPressTimeout))

        verify(longClickableView, never()).performClick()
    }

    @Test
    fun `does not synthesize click after cancel`() {
        underTest(view, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, 0L))
        underTest(view, obtain(MotionEvent.ACTION_CANCEL, 0f, 0f, 20L))
        underTest(view, obtain(MotionEvent.ACTION_UP, 0f, 0f, 50L))

        verify(view, never()).performClick()
    }

    @Test
    fun `does not synthesize click when view is still unfocused after post`() {
        val unfocusedView = mock<View> {
            on { isFocusable } doReturn true
            on { isFocusableInTouchMode } doReturn true
            on { isClickable } doReturn true
            on { isLongClickable } doReturn false
            on { isFocused } doReturn false
            on { post(any()) } doAnswer { invocation ->
                (invocation.arguments[0] as Runnable).run()
                true
            }
        }

        underTest(unfocusedView, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, 0L))
        underTest(unfocusedView, obtain(MotionEvent.ACTION_UP, 0f, 0f, 50L))

        verify(unfocusedView, never()).performClick()
    }

    @Test
    fun `onTouch always returns false`() {
        assertFalse(underTest(view, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, 0L)))
        assertFalse(underTest(view, obtain(MotionEvent.ACTION_MOVE, 1f, 1f, 10L)))
        assertFalse(underTest(view, obtain(MotionEvent.ACTION_UP, 1f, 1f, 50L)))
        assertFalse(underTest(view, obtain(MotionEvent.ACTION_CANCEL, 1f, 1f, 50L)))
    }

    private fun dispatchTap(downTime: Long, upTime: Long) {
        underTest(view, obtain(MotionEvent.ACTION_DOWN, 0f, 0f, downTime))
        underTest(view, obtain(MotionEvent.ACTION_UP, 0f, 0f, upTime))
    }

    private fun obtain(action: Int, x: Float, y: Float, eventTime: Long): MotionEvent {
        return MotionEvent.obtain(/* downTime */ 0L, eventTime, action, x, y, /* metaState */ 0)
    }
}
