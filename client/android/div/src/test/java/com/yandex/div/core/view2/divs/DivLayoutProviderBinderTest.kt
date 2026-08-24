package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivLayoutProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

private const val HEIGHT_VARIABLE_NAME = "height"
private const val WIDTH_VARIABLE_NAME = "width"
private const val EXPECTED_HEIGHT = 300L
private const val EXPECTED_WIDTH = 100L
private const val LEFT = 10
private const val TOP = 20
private const val RIGHT = 110
private const val BOTTOM = 320

@RunWith(RobolectricTestRunner::class)
class DivLayoutProviderBinderTest {

    private var onPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null
    private val onPreDrawListenerCaptor = argumentCaptor<ViewTreeObserver.OnPreDrawListener>()
    private val viewTreeObserver = mock<ViewTreeObserver> {
        on { addOnPreDrawListener(onPreDrawListenerCaptor.capture()) } doAnswer {
            onPreDrawListener = onPreDrawListenerCaptor.lastValue
        }
    }
    private val divData = DivData(logId = "", states = emptyList())
    private val divView = mock<Div2View> {
        on { viewTreeObserver } doReturn viewTreeObserver
        on { divData } doReturn divData
        on { dataTag } doReturn mock()
        on { expressionResolver } doReturn mock()
        on { errorCollector } doReturn mock()
    }
    private val variableMutationHandler = mock<VariableMutationHandler> {
        on { setVariable(any(), any(), any(), any()) } doAnswer {
            val value = it.arguments[1] as String
            when (it.arguments[0]) {
                HEIGHT_VARIABLE_NAME -> heightVariable.set(value.toLong())
                WIDTH_VARIABLE_NAME -> widthVariable.set(value.toLong())
            }
            null
        }
    }
    private val underTest = DivLayoutProviderBinder(variableMutationHandler)
    private val layoutProvider = DivLayoutProvider(HEIGHT_VARIABLE_NAME, WIDTH_VARIABLE_NAME)
    private val resolver = mock<ExpressionResolver>()
    private val metrics = DisplayMetrics().apply { density = 1f }
    private val resources = mock<Resources> {
        on { displayMetrics } doReturn metrics
    }
    private val layoutChangeListener = argumentCaptor<View.OnLayoutChangeListener>()
    private val view = mock<View> {
        on { resources } doReturn resources
        on { addOnLayoutChangeListener(layoutChangeListener.capture()) } doAnswer {}
    }

    init {
        heightVariable.set(0L)
        widthVariable.set(0L)
    }

    @Test
    fun `height variable is updated when height changed`() {
        bind()

        layout(onlyHeight = true)

        Assert.assertEquals(EXPECTED_HEIGHT, heightVariable.getValue())
        Assert.assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `width variable is updated when width changed`() {
        bind()

        layout(onlyWidth = true)

        Assert.assertEquals(0L, heightVariable.getValue())
        Assert.assertEquals(EXPECTED_WIDTH, widthVariable.getValue())
    }

    @Test
    fun `update variables when sizes changed`() {
        bind()

        layout()

        Assert.assertEquals(EXPECTED_HEIGHT, heightVariable.getValue())
        Assert.assertEquals(EXPECTED_WIDTH, widthVariable.getValue())
    }

    @Test
    fun `update height variable when width is not provided`() {
        val layoutProvider = DivLayoutProvider(HEIGHT_VARIABLE_NAME, null)
        underTest.bind(view, layoutProvider, null, resolver, divView)

        layout()

        Assert.assertEquals(EXPECTED_HEIGHT, heightVariable.getValue())
        Assert.assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `update width variable when height is not provided`() {
        val layoutProvider = DivLayoutProvider(null, WIDTH_VARIABLE_NAME)
        underTest.bind(view, layoutProvider, null, resolver, divView)

        layout()

        Assert.assertEquals(0L, heightVariable.getValue())
        Assert.assertEquals(EXPECTED_WIDTH, widthVariable.getValue())
    }

    @Test
    fun `update variables after reattach`() {
        bind()
        underTest.onAttach(divView)
        underTest.onDetach(divView)

        layout()

        Assert.assertEquals(EXPECTED_HEIGHT, heightVariable.getValue())
        Assert.assertEquals(EXPECTED_WIDTH, widthVariable.getValue())
    }

    @Test
    fun `remove onPreDrawListener on detach`() {
        whenever(divView.isAttachedToWindow).doReturn(true)
        bind()

        underTest.onDetach(divView)

        verify(viewTreeObserver).removeOnPreDrawListener(eq(onPreDrawListener))
    }

    @Test
    fun `remove onPreDrawListener on detach after attach`() {
        bind()
        underTest.onAttach(divView)

        underTest.onDetach(divView)

        verify(viewTreeObserver).removeOnPreDrawListener(eq(onPreDrawListener))
    }

    @Test
    fun `remove onPreDrawListener on detach after reattach`() {
        bind()
        underTest.onAttach(divView)
        underTest.onDetach(divView)
        onPreDrawListener = null
        underTest.onAttach(divView)

        underTest.onDetach(divView)

        verify(viewTreeObserver).removeOnPreDrawListener(eq(onPreDrawListener))
    }

    @Test
    fun `not listen to predraw when no has providers`() {
        underTest.onAttach(divView)
        verify(viewTreeObserver, never()).addOnPreDrawListener(any())
    }

    @Test
    fun `not listen to predraw when has no providers on rebind`() {
        bind()
        underTest.bind(view, null, layoutProvider, resolver, divView)

        underTest.onAttach(divView)

        verify(viewTreeObserver, never()).addOnPreDrawListener(any())
    }

    private fun bind() = underTest.bind(view, layoutProvider, null, resolver, divView)

    private fun layout(onlyWidth: Boolean = false, onlyHeight: Boolean = false) {
        underTest.onAttach(divView)
        layoutChangeListener.lastValue.onLayoutChange(
            view,
            if (onlyHeight) 0 else LEFT,
            if (onlyWidth) 0 else TOP,
            if (onlyHeight) 0 else RIGHT,
            if (onlyWidth) 0 else BOTTOM,
            0, 0, 0, 0
        )
        onPreDrawListener?.onPreDraw()
    }

    companion object {
        private val heightVariable = Variable.IntegerVariable(HEIGHT_VARIABLE_NAME, 0L)
        private val widthVariable = Variable.IntegerVariable(WIDTH_VARIABLE_NAME, 0L)
    }
}
