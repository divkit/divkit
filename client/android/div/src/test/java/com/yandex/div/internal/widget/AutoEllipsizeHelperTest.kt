package com.yandex.div.internal.widget

import android.view.ViewTreeObserver
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
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
    private val textView = mock<EllipsizedTextView> {
        on { viewTreeObserver } doReturn viewTreeObserver
        on { height } doReturn 50
        on { lineHeight } doReturn 5
        on { lineCount } doReturn 5
        on { fixedLineHeight } doReturn UNDEFINED_LINE_HEIGHT
    }

    private val underTest = AutoEllipsizeHelper(textView).apply {
        isEnabled = true
    }

    @Test
    fun `remove pre draw listener when max lines didn't change`() {
        whenever(textView.maxLines).thenReturn(5)

        underTest.onViewAttachedToWindow()

        Assert.assertTrue(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    @Test
    fun `remove pre draw listener on detach`() {
        underTest.onViewAttachedToWindow()
        underTest.onViewDetachedFromWindow()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListenerCaptor.firstValue)
    }

    @Test
    fun `do nothing when become disabled`() {
        underTest.onViewAttachedToWindow()
        underTest.isEnabled = false

        Assert.assertTrue(preDrawListenerCaptor.firstValue.onPreDraw())
    }

    @Test
    fun `constraint visible lines by layout lineCount`() {
        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 5
    }

    @Test
    fun `constraint visible lines by lines with fixed height`() {
        whenever(textView.fixedLineHeight).doReturn(15)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 3
    }

    @Test
    fun `constraint visible lines by view height`() {
        whenever(textView.lineHeight).doReturn(15)

        underTest.onViewAttachedToWindow()

        Assert.assertFalse(preDrawListenerCaptor.firstValue.onPreDraw())
        verify(textView).maxLines = 3
    }
}
