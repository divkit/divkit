package com.yandex.div.core.widget

import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdaptiveMaxLinesTest {

    private val attachStateChangeListenerCaptor = argumentCaptor<View.OnAttachStateChangeListener>()
    private val preDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val viewTreeObserver = mock<ViewTreeObserver>()
    private val textView = mock<TextView> {
        on { viewTreeObserver } doReturn viewTreeObserver
    }

    private val underTest = AdaptiveMaxLines(textView, DrawingPassOverrideStrategy.Default)

    @Test
    fun `add pre draw listener when view already attached to window`() {
        whenever(textView.isAttachedToWindow).thenReturn(true)
        whenever(textView.windowToken).thenReturn(mock())
        underTest.apply(TEST_PARAMS)

        verify(viewTreeObserver).addOnPreDrawListener(preDrawListenerCaptor.capture())
    }

    @Test
    fun `add pre draw listener when view attached to window`() {
        underTest.apply(TEST_PARAMS)

        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()
    }

    @Test
    fun `do not ellipsize text when line count is less then total visible lines`() {
        underTest.apply(TEST_PARAMS)
        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()

        whenever(textView.text).thenReturn(TEST_TEXT)
        whenever(textView.lineCount).thenReturn(6)
        val proceed = preDrawListenerCaptor.firstValue.onPreDraw()
        Assert.assertFalse(proceed)

        verify(textView).maxLines = Int.MAX_VALUE
    }

    @Test
    fun `ellipsize text when line count is less then total visible lines`() {
        underTest.apply(TEST_PARAMS)
        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()

        whenever(textView.text).thenReturn(TEST_TEXT)
        whenever(textView.lineCount).thenReturn(8)
        val proceed = preDrawListenerCaptor.firstValue.onPreDraw()
        Assert.assertFalse(proceed)

        verify(textView).maxLines = TEST_PARAMS.maxLines
    }

    @Test
    fun `wait until text set`() {
        underTest.apply(TEST_PARAMS)
        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()

        val proceed = preDrawListenerCaptor.firstValue.onPreDraw()
        Assert.assertTrue(proceed)
    }

    @Test
    fun `remove pre draw listener when adapt requested`() {
        underTest.apply(TEST_PARAMS)
        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()

        whenever(textView.text).thenReturn(TEST_TEXT)
        whenever(textView.lineCount).thenReturn(7)
        var proceed = preDrawListenerCaptor.firstValue.onPreDraw()
        Assert.assertFalse(proceed)

        proceed = preDrawListenerCaptor.firstValue.onPreDraw()
        Assert.assertTrue(proceed)

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    @Test
    fun `reset removes listeners`() {
        underTest.apply(TEST_PARAMS)
        whenViewAttachedToWindow()
        whenPreDrawListenerAdded()

        underTest.reset()

        verify(textView).removeOnAttachStateChangeListener(attachStateChangeListenerCaptor.firstValue)
        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    private fun whenViewAttachedToWindow() {
        verify(textView).addOnAttachStateChangeListener(attachStateChangeListenerCaptor.capture())
        attachStateChangeListenerCaptor.firstValue.onViewAttachedToWindow(textView)
    }

    private fun whenPreDrawListenerAdded() {
        verify(viewTreeObserver).addOnPreDrawListener(preDrawListenerCaptor.capture())
    }

    private companion object {
        private val TEST_PARAMS = AdaptiveMaxLines.Params(maxLines = 3, minHiddenLines = 4)
        private const val TEST_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
    }
}
