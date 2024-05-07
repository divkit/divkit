package com.yandex.div.gesture

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [DivGestureExtensionHandler].
 */
@RunWith(RobolectricTestRunner::class)
internal class DivGestureExtensionHandlerTest {

    private val div2View = mock<Div2View> {
        on { expressionResolver } doReturn ExpressionResolver.EMPTY
    }
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val view = mock<View> {
        on { context } doReturn activity
    }
    private val mockErrorLogger = mock<ParsingErrorLogger>()
    private val mockErrorLoggerFactory = mock<ParsingErrorLoggerFactory> {
        on { create(any()) } doReturn mockErrorLogger
    }
    private val extensionParams = JSONObject(
        """
            {
                "swipe_up": [
                    {
                        "log_id": "on_swipe_up",
                        "url": "https://deep.link"
                    }
                ],
                "swipe_down": [
                    {
                        "log_id": "on_swipe_down",
                        "url": "https://deep.link"
                    }
            
                ],
                "swipe_left": [],
                "swipe_right": []
            }
        """.trimIndent()
    )
    private val divExtensions = listOf(
        DivExtension(id = "gesture", params = extensionParams)
    )
    private val divWithExtension = mock<DivBase> {
        on { extensions } doReturn divExtensions
    }

    private val resolver = ExpressionResolver.EMPTY
    private val underTest = DivGestureExtensionHandler(mockErrorLoggerFactory)

    @Test
    fun `matches returns true when gesture extension exists`() {
        Assert.assertTrue(underTest.matches(divWithExtension))
    }

    @Test
    fun `matches returns false when gesture extension does not exist`() {
        whenever(divWithExtension.extensions).thenReturn(emptyList())
        Assert.assertFalse(underTest.matches(divWithExtension))
    }

    @Test
    fun `unbind view removes touch listener`() {
        underTest.unbindView(div2View, resolver, view, divWithExtension)
        verify(view).setOnTouchListener(null)
    }

    @Test
    fun `bind view will attach own touch listener`() {
        underTest.bindView(div2View, resolver, view, divWithExtension)
        verify(view).setOnTouchListener(any())
    }

    @Test
    fun `swipe-like motion events will trigger corresponding action`() {
        underTest.bindView(div2View, resolver, view, divWithExtension)

        val touchListener = argumentCaptor<View.OnTouchListener>()
        verify(view).setOnTouchListener(touchListener.capture())
        emulateSwipeGesture(touchListener.lastValue)

        verify(div2View).handleAction(any(), any(), any())
    }

    private fun emulateSwipeGesture(touchListener: View.OnTouchListener) {
        touchListener.apply {
            onTouch(view, mockMotion(MotionEvent.ACTION_DOWN, y = 0f))
            onTouch(view, mockMotion(MotionEvent.ACTION_MOVE, y = 100f))
            onTouch(view, mockMotion(MotionEvent.ACTION_UP, y = 100f))
        }
    }

    private fun mockMotion(action: Int, y: Float) = mock<MotionEvent> {
        on { getAction() } doReturn action
        on { rawY } doReturn y
        on { getY() } doReturn y
    }
}