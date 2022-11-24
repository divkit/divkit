package com.yandex.div.internal.widget

import android.text.Layout
import android.view.ViewTreeObserver
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AutoEllipsizeHelperTest {

    private val preDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { addOnPreDrawListener(preDrawListenerCaptor.capture()) } doAnswer {}
    }
    private val layout = mock<Layout> {
        on { lineCount } doReturn 20
    }
    private val textView = mock<EllipsizedTextView> {
        on { layout } doReturn layout
        on { viewTreeObserver } doReturn viewTreeObserver
    }

    private val underTest = AutoEllipsizeHelper(textView)

    @Test
    fun `update max lines based on text params`() {
        underTest.isEnabled = true
        whenever(textView.height).thenReturn(50)
        whenever(textView.lineHeight).thenReturn(5)
        whenever(layout.getLineBottom(9)).thenReturn(50)
        whenever(layout.getLineBottom(10)).thenReturn(55)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 10
    }

    @Test
    fun `remove pre draw listener when max lines didn't change`() {
        underTest.isEnabled = true
        whenever(textView.maxLines).thenReturn(10)
        whenever(textView.height).thenReturn(50)
        whenever(textView.lineHeight).thenReturn(5)
        whenever(layout.getLineBottom(9)).thenReturn(50)
        whenever(layout.getLineBottom(10)).thenReturn(55)

        underTest.onViewAttachedToWindow()

        Assert.assertTrue(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    @Test
    fun `remove pre draw listener on detach`() {
        underTest.isEnabled = true

        underTest.onViewAttachedToWindow()
        underTest.onViewDetachedFromWindow()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    @Test
    fun `do nothing when become disabled`() {
        underTest.isEnabled = true

        underTest.onViewAttachedToWindow()
        underTest.isEnabled = false

        Assert.assertTrue(preDrawListenerCaptor.firstValue.onPreDraw())
    }

    @Test
    fun `allow to overflow text by allowed threshold`() {
        underTest.isEnabled = true
        whenever(textView.height).thenReturn(50)
        whenever(textView.lineHeight).thenReturn(5)
        whenever(layout.getLineBottom(9)).thenReturn(50)
        whenever(layout.getLineBottom(10)).thenReturn(55)
        whenever(textView.paddingTop).thenReturn(1)
        whenever(textView.paddingBottom).thenReturn(2)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 10
    }

    @Test
    fun `reduce max lines if text overflows more than allowed threshold`() {
        underTest.isEnabled = true
        whenever(textView.height).thenReturn(50)
        whenever(textView.lineHeight).thenReturn(5)
        whenever(layout.getLineBottom(9)).thenReturn(50)
        whenever(layout.getLineBottom(10)).thenReturn(55)
        whenever(textView.paddingTop).thenReturn(2)
        whenever(textView.paddingBottom).thenReturn(2)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 9
    }

    @Test
    fun `constraint visible lines by layout lineCount`() {
        underTest.isEnabled = true
        whenever(textView.height).thenReturn(50)
        whenever(textView.lineHeight).thenReturn(5)
        whenever(layout.lineCount).thenReturn(5)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(layout).getLineBottom(4)
    }

    @Test
    fun `return true when layout is null`() {
        underTest.isEnabled = true
        whenever(textView.layout).thenReturn(null)

        underTest.onViewAttachedToWindow()

        Assert.assertTrue(preDrawListenerCaptor.firstValue.onPreDraw())
    }
}
