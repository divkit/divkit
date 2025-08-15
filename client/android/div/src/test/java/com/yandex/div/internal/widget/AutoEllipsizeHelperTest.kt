package com.yandex.div.internal.widget

import android.text.Layout
import android.view.ViewTreeObserver
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class AutoEllipsizeHelperTest {

    private val preDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val preDrawListener: ViewTreeObserver.OnPreDrawListener
        get() = preDrawListenerCaptor.firstValue

    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { addOnPreDrawListener(preDrawListenerCaptor.capture()) } doAnswer {}
    }

    private var lineHeight = TEXT_VIEW_DEFAULT_LINE_HEIGHT

    private val textLayout = mock<Layout> {
        on { lineCount } doReturn 4096

        on { getLineTop(anyInt()) } doAnswer { invocationOnMock ->
            val line = invocationOnMock.arguments.first() as Int
            line * lineHeight
        }

        on { getLineForVertical(anyInt()) } doAnswer { invocationOnMock ->
            val vertical = invocationOnMock.arguments.first() as Int
            vertical / lineHeight
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private val textView = mock<EllipsizedTextView> {
        on { layout } doReturn textLayout
        on { height } doReturn TEXT_VIEW_HEIGHT
        on { getLineHeight() } doReturn lineHeight
        on { setLineHeight(anyInt()) } doAnswer { invocationOnMock ->
            lineHeight = invocationOnMock.arguments.first() as Int
        }
        on { lineCount } doAnswer { textLayout.lineCount }
        on { maxLines } doReturn -1
        on { viewTreeObserver } doReturn viewTreeObserver
    }

    private val underTest = AutoEllipsizeHelper(textView).apply {
        isEnabled = true
    }

    @Test
    fun `remove layout listener when text lines fits view`() {
        whenever(textLayout.lineCount).thenReturn(6)

        underTest.onViewAttachedToWindow()
        preDrawListener.onPreDraw()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `remove layout listener on detach`() {
        underTest.onViewAttachedToWindow()
        underTest.onViewDetachedFromWindow()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `do nothing when become disabled`() {
        underTest.onViewAttachedToWindow()
        underTest.isEnabled = false
        clearInvocations(textView)

        preDrawListener.onPreDraw()

        verifyNoMoreInteractions(textView)
    }

    @Test
    fun `constraint visible lines by layout lineCount`() {
        underTest.onViewAttachedToWindow()
        preDrawListener.onPreDraw()

        verify(textView).maxLines = eq(6)
    }

    @Test
    fun `constraint visible lines by view height`() {
        textView.lineHeight = TEXT_VIEW_INCREASED_LINE_HEIGHT

        underTest.onViewAttachedToWindow()
        preDrawListener.onPreDraw()

        verify(textView).maxLines = eq(3)
    }

    private companion object {
        const val TEXT_VIEW_HEIGHT = 50
        const val TEXT_VIEW_DEFAULT_LINE_HEIGHT = 8
        const val TEXT_VIEW_INCREASED_LINE_HEIGHT = 16
    }
}
