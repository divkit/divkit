package com.yandex.div.internal.widget

import android.text.Layout
import android.view.ViewTreeObserver
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class AutoEllipsizeHelperTest {

    private val preDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val preDrawListener: ViewTreeObserver.OnPreDrawListener
        get() = preDrawListenerCaptor.lastValue

    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { addOnPreDrawListener(preDrawListenerCaptor.capture()) } doAnswer {}
    }

    private var isAttachedToWindow = false
    private var measuredWidth = TEXT_VIEW_WIDTH
    private var measuredHeight = TEXT_VIEW_HEIGHT
    private var layoutHeight = TEXT_VIEW_HEIGHT
    private var lineHeight = TEXT_VIEW_DEFAULT_LINE_HEIGHT
    private var untruncatedLineCount = TEXT_VIEW_UNTRUNCATED_LINE_COUNT
    private var maxLines = -1

    private val textLayout = mock<Layout> {
        on { lineCount } doReturn TEXT_LAYOUT_LINE_COUNT
        on { height } doAnswer { layoutHeight }
        on { getLineTop(any()) } doAnswer { invocationOnMock ->
            val line = invocationOnMock.arguments.first() as Int
            line * lineHeight
        }
        on { getLineForVertical(any()) } doAnswer { invocationOnMock ->
            val vertical = invocationOnMock.arguments.first() as Int
            vertical / lineHeight
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private val textView = mock<EllipsizedTextView> {
        on { isAttachedToWindow } doAnswer { isAttachedToWindow }
        on { measuredWidth } doAnswer { measuredWidth }
        on { measuredHeight } doAnswer { measuredHeight }
        on { layout } doReturn textLayout
        on { height } doReturn TEXT_VIEW_HEIGHT
        on { getLineHeight() } doReturn lineHeight
        on { setLineHeight(any()) } doAnswer { invocationOnMock ->
            lineHeight = invocationOnMock.arguments.first() as Int
        }
        on { lineCount } doReturn TEXT_VIEW_RENDERED_LINE_COUNT
        on { untruncatedLineCount } doAnswer { untruncatedLineCount }
        on { maxLines } doAnswer { maxLines }
        on { viewTreeObserver } doReturn viewTreeObserver
    }

    private val underTest = AutoEllipsizeHelper(textView).apply {
        isEnabled = true
    }

    @Test
    fun `add pre draw listener when view is attached`() {
        underTest.onViewAttachedToWindow()

        verify(viewTreeObserver).addOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `remove pre draw listener when text lines fit view`() {
        untruncatedLineCount = TEXT_VIEW_VISIBLE_LINE_COUNT
        underTest.onViewAttachedToWindow()

        preDrawListener.onPreDraw()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `remove pre draw listener on detach`() {
        underTest.onViewAttachedToWindow()

        underTest.onViewDetachedFromWindow()

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `disabling removes pre draw listener and makes captured callback inert`() {
        underTest.onViewAttachedToWindow()

        underTest.isEnabled = false

        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
        clearInvocations(textView)
        preDrawListener.onPreDraw()
        verifyNoMoreInteractions(textView)
    }

    @Test
    fun `setting the same enabled value keeps the installed listener`() {
        underTest.onViewAttachedToWindow()
        clearInvocations(viewTreeObserver)

        underTest.isEnabled = true

        verifyNoMoreInteractions(viewTreeObserver)
    }

    @Test
    fun `measurement does nothing when auto ellipsize is disabled`() {
        isAttachedToWindow = true
        val disabledHelper = AutoEllipsizeHelper(textView)

        disabledHelper.onViewMeasured(textRevision = 1)

        verifyNoMoreInteractions(viewTreeObserver)
    }

    @Test
    fun `measurement does nothing when view is detached`() {
        underTest.onViewMeasured(textRevision = 1)

        verifyNoMoreInteractions(viewTreeObserver)
    }

    @Test
    fun `measurement installs pre draw listener for attached view`() {
        isAttachedToWindow = true

        underTest.onViewMeasured(textRevision = 1)

        verify(viewTreeObserver).addOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `unchanged measurement inputs do not reinstall removed listener`() {
        // Arrange
        isAttachedToWindow = true
        untruncatedLineCount = TEXT_VIEW_VISIBLE_LINE_COUNT
        underTest.onViewMeasured(textRevision = 1)
        preDrawListener.onPreDraw()
        clearInvocations(viewTreeObserver)

        // Act
        underTest.onViewMeasured(textRevision = 1)

        // Assert
        verifyNoMoreInteractions(viewTreeObserver)
    }

    @Test
    fun `changed measurement inputs reinstall removed listener`() {
        // Arrange
        isAttachedToWindow = true
        untruncatedLineCount = TEXT_VIEW_VISIBLE_LINE_COUNT
        underTest.onViewMeasured(textRevision = 1)
        preDrawListener.onPreDraw()
        measuredWidth++
        clearInvocations(viewTreeObserver)

        // Act
        underTest.onViewMeasured(textRevision = 1)

        // Assert
        verify(viewTreeObserver).addOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `changed text revision reinstalls removed listener`() {
        // Arrange
        isAttachedToWindow = true
        untruncatedLineCount = TEXT_VIEW_VISIBLE_LINE_COUNT
        underTest.onViewMeasured(textRevision = 1)
        preDrawListener.onPreDraw()
        clearInvocations(viewTreeObserver)

        // Act
        underTest.onViewMeasured(textRevision = 2)

        // Assert
        verify(viewTreeObserver).addOnPreDrawListener(preDrawListener)
    }

    @Test
    fun `reenabling resets cached measurement inputs`() {
        // Arrange
        isAttachedToWindow = true
        untruncatedLineCount = TEXT_VIEW_VISIBLE_LINE_COUNT
        underTest.onViewMeasured(textRevision = 1)
        preDrawListener.onPreDraw()
        underTest.isEnabled = false
        underTest.isEnabled = true

        // Act
        underTest.onViewMeasured(textRevision = 1)

        // Assert
        verify(viewTreeObserver, times(2)).addOnPreDrawListener(any())
    }

    @Test
    fun `untruncated line count allows reducing max lines`() {
        underTest.onViewAttachedToWindow()

        preDrawListener.onPreDraw()

        verify(textView).maxLines = eq(TEXT_VIEW_VISIBLE_LINE_COUNT)
    }

    @Test
    fun `view height determines reduced max lines`() {
        textView.lineHeight = TEXT_VIEW_INCREASED_LINE_HEIGHT
        underTest.onViewAttachedToWindow()

        preDrawListener.onPreDraw()

        verify(textView).maxLines = eq(TEXT_VIEW_VISIBLE_LINES_WITH_INCREASED_HEIGHT)
    }

    @Test
    fun `visible line count does not increase configured max lines`() {
        maxLines = TEXT_VIEW_CONFIGURED_MAX_LINES
        underTest.onViewAttachedToWindow()

        preDrawListener.onPreDraw()

        verify(textView, never()).maxLines = any()
        verify(viewTreeObserver).removeOnPreDrawListener(preDrawListener)
    }

    private companion object {
        const val TEXT_VIEW_WIDTH = 100
        const val TEXT_VIEW_HEIGHT = 50
        const val TEXT_VIEW_DEFAULT_LINE_HEIGHT = 8
        const val TEXT_VIEW_INCREASED_LINE_HEIGHT = 16
        const val TEXT_VIEW_VISIBLE_LINE_COUNT = 6
        const val TEXT_VIEW_VISIBLE_LINES_WITH_INCREASED_HEIGHT = 3
        const val TEXT_VIEW_RENDERED_LINE_COUNT = 2
        const val TEXT_VIEW_UNTRUNCATED_LINE_COUNT = 8
        const val TEXT_VIEW_CONFIGURED_MAX_LINES = 3
        const val TEXT_LAYOUT_LINE_COUNT = 4096
    }
}
