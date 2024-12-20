package com.yandex.div.datetime

import android.app.Activity
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivContainer
import com.yandex.div2.DivExtension
import com.yandex.div2.DivInput
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [DivDateTimeExtensionHandler].
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

    private val extensionParams = JSONObject(
        """
            {
                "text_variable": "selected_date",
                "mode": "date"
            }
        """.trimIndent()
    )
    private val divExtensions = listOf(
        DivExtension(id = DivDateTimeExtensionHandler.EXTENSION_ID, params = extensionParams)
    )

    private val divInput = mock<DivInput> {
        on { extensions } doReturn divExtensions
    }

    private val resolver = ExpressionResolver.EMPTY
    private val extensionHandler = DivDateTimeExtensionHandler()


    @Test
    fun `GIVEN DivContainer with datetime extension WHEN check for match THEN return false`() {

        val containerDivWithDateTimeExtension = mock<DivContainer> {
            on { extensions } doReturn divExtensions
        }

        Assert.assertFalse(extensionHandler.matches(containerDivWithDateTimeExtension))
    }

    @Test
    fun `GIVEN DivInput with datetime extension WHEN check for match THEN return true`() {
        Assert.assertTrue(extensionHandler.matches(divInput))
    }

    @Test
    fun `GIVEN DivInput without datetime extension WHEN check for match THEN return false`() {
        whenever(divInput.extensions).thenReturn(emptyList())
        Assert.assertFalse(extensionHandler.matches(divInput))
    }

    @Test
    fun `GIVEN DivInput with datetime extension WHEN bind THEN set focusable to false`() {
        extensionHandler.bindView(div2View, resolver, view, divInput)
        verify(view).isFocusable = false
    }

    @Test
    fun `WHEN bind THEN set click listener`() {
        extensionHandler.bindView(div2View, resolver, view, divInput)
        verify(view).setOnClickListener(any())
    }

    @Test
    fun `WHEN unbind THEN set focusable back to initial value`() {
        whenever(view.isFocusable).thenReturn(true)
        extensionHandler.bindView(div2View, resolver, view, divInput)
        extensionHandler.unbindView(div2View, resolver, view, divInput)
        verify(view).isFocusable = true
    }

    @Test
    fun `WHEN unbind THEN reset click listener`() {
        extensionHandler.unbindView(div2View, resolver, view, divInput)
        verify(view).setOnClickListener(null)
    }
}