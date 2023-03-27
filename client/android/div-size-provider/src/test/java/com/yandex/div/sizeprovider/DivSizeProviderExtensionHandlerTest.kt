package com.yandex.div.sizeprovider

import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

private const val HEIGHT_VARIABLE = "height_variable"
private const val WIDTH_VARIABLE = "width_variable"

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [DivSizeProviderExtensionHandlerTest.ShadowDivSizeProviderVariablesHolder::class])
class DivSizeProviderExtensionHandlerTest {

    private val div = mock<DivBase>()
    private val listener = argumentCaptor<View.OnLayoutChangeListener>()
    private val view = mock<View> {
        on { addOnLayoutChangeListener(listener.capture()) } doAnswer {}
    }
    private val data = DivData("", emptyList())
    private val onPreDrawListener = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { addOnPreDrawListener(onPreDrawListener.capture()) } doAnswer {}
    }
    private val divView = mock<Div2View> {
        on { divData } doReturn data
        on { expressionResolver } doReturn mock()
        on { viewTreeObserver } doReturn viewTreeObserver
    }
    private val underTest = DivSizeProviderExtensionHandler()

    @Before
    fun setup() {
        changedVariables.clear()
    }

    @Test
    fun `not match when no extensions`() {
        Assert.assertFalse(underTest.matches(div))
    }

    @Test
    fun `not match when extension id is wrong`() {
        whenever(div.extensions).doReturn(listOf(DivExtension("some_extension")))
        Assert.assertFalse(underTest.matches(div))
    }

    @Test
    fun `matches when extension id is right`() {
        setExtension()
        Assert.assertTrue(underTest.matches(div))
    }

    @Test
    fun `not listen to layout changes when got no variables`() {
        setExtension()
        verify(view, never()).addOnLayoutChangeListener(any())
    }

    @Test
    fun `not update width when layout caused by size listener`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()
        changedVariables.add(WIDTH_VARIABLE)

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 0, 50)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView, never()).setVariable(eq(WIDTH_VARIABLE), any())
    }

    @Test
    fun `not update height when layout caused by size listener`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()
        changedVariables.add(HEIGHT_VARIABLE)

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 100, 0)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView, never()).setVariable(eq(HEIGHT_VARIABLE), any())
    }

    @Test
    fun `update variable when width changes`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 0, 50)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView).setVariable(WIDTH_VARIABLE, "100")
    }

    @Test
    fun `update variable when height changes`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 100, 0)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView).setVariable(HEIGHT_VARIABLE, "50")
    }

    @Test
    fun `not update variable when width was not changed`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 100, 0)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView, never()).setVariable(eq(WIDTH_VARIABLE), any())
    }

    @Test
    fun `not update variable when height was not changed`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 0, 50)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView, never()).setVariable(eq(HEIGHT_VARIABLE), any())
    }

    @Test
    fun `update both variables when both changes`() {
        setExtension(WIDTH_VARIABLE, HEIGHT_VARIABLE)
        bindExtension()

        listener.firstValue.onLayoutChange(view, 0, 0, 100, 50, 0, 0, 0, 0)
        onPreDrawListener.firstValue.onPreDraw()

        verify(divView).setVariable(WIDTH_VARIABLE, "100")
        verify(divView).setVariable(HEIGHT_VARIABLE, "50")
    }

    @Test
    fun `stop listen to layout changes on unbind`() {
        setExtension(WIDTH_VARIABLE)
        underTest.bindView(divView, view, div)

        underTest.unbindView(divView, view, div)

        verify(view).removeOnLayoutChangeListener(anyOrNull())
    }

    private fun setExtension(widthVariable: String? = null, heightVariable: String? = null) =
        whenever(div.extensions).doReturn(listOf(
            DivExtension(SIZE_PROVIDER_EXTENSION_ID, JSONObject().apply {
                widthVariable?.let { put(SIZE_PROVIDER_PARAM_WIDTH, it) }
                heightVariable?.let { put(SIZE_PROVIDER_PARAM_HEIGHT, it) }
            })
        ))

    private fun bindExtension() = underTest.bindView(divView, view, div)

    companion object {
        private val changedVariables = mutableListOf<String>()
    }

    @Implements(DivSizeProviderVariablesHolder::class)
    class ShadowDivSizeProviderVariablesHolder {
        @Implementation
        fun contains(variable: String) = changedVariables.contains(variable)
    }
}
