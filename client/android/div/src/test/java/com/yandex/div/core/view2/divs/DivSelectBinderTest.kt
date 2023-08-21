package com.yandex.div.core.view2.divs

import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.expression.variables.TwoWayVariableBinder
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.widgets.DivSelectView
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.widget.SelectView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivSelect
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivSelectBinderTest : DivBinderTest() {
    private val divTypefaceResolver = mock<DivTypefaceResolver>()
    private val variableBinder = mock<TwoWayStringVariableBinder>() {
        on { bindVariable(any(), any(), any()) } doReturn mock()
    }
    private val errorCollectors = mock<ErrorCollectors> {
        on { getOrCreate(anyOrNull(), anyOrNull()) } doReturn mock()
    }
    private val captor = argumentCaptor<TwoWayVariableBinder.Callbacks<String>>()

    private val underTest = DivSelectBinder(
        baseBinder = baseBinder,
        typefaceResolver = divTypefaceResolver,
        variableBinder = variableBinder,
        errorCollectors = errorCollectors
    )

    private val div = UnitTestData(SELECT_DIR, "with_options.json").div as Div.Select
    private val divSelect = div.value
    private val view = (viewCreator.create(div, ExpressionResolver.EMPTY) as DivSelectView).apply {
        layoutParams = defaultLayoutParams()
    }

    @Test
    fun `bind value_variable`() {
        underTest.bindView(view, divSelect, divView)

        verify(variableBinder).bindVariable(any(), eq(divSelect.valueVariable), any())
        verifyNoMoreInteractions(variableBinder)
    }

    @Test
    fun `update text after variable changed`() {
        underTest.bindView(view, divSelect, divView)
        verify(variableBinder).bindVariable(any(), any(), captor.capture())

        val (optionText, optionValue) = divSelect.options.evaluateLastOption()

        view.assertTextApplied(optionText) {
            val callbacks = captor.allValues.single()
            callbacks.onVariableChanged(optionValue)
        }

        verifyNoMoreInteractions(variableBinder)
    }

    @Test
    fun `update text and variable after option selected`() {
        val viewStateChangeListener = mock<(String) -> Unit>()

        underTest.bindView(view, divSelect, divView)
        verify(variableBinder).bindVariable(any(), any(), captor.capture())

        val (optionText, optionValue) = divSelect.options.evaluateLastOption()

        view.assertTextApplied(optionText) {
            val callbacks = captor.allValues.single()
            callbacks.setViewStateChangeListener(viewStateChangeListener)

            view.onItemSelectedListener!!(divSelect.options.size - 1)
        }

        verify(viewStateChangeListener).invoke(optionValue)
        verifyNoMoreInteractions(viewStateChangeListener)

        verifyNoMoreInteractions(variableBinder)
    }

    private fun List<DivSelect.Option>.evaluateLastOption(): Pair<String, String> {
        val option = last()

        val optionValue = option.value.evaluate(divView.expressionResolver)
        val optionText = option.text?.evaluate(divView.expressionResolver) ?: optionValue

        return optionText to optionValue
    }

    private inline fun SelectView.assertTextApplied(expectedText: String, body: () -> Unit) {
        Assert.assertNotEquals(text, expectedText)
        body()
        Assert.assertEquals(text, expectedText)
    }

    companion object {
        private const val SELECT_DIR = "div-select"
    }
}
