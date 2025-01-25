package com.yandex.div.core.view2.divs

import com.yandex.div.core.expression.variables.TwoWayBooleanVariableBinder
import com.yandex.div.core.expression.variables.TwoWayVariableBinder
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.widgets.DivSwitchView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivSwitch
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivSwitchBinderTest : DivBinderTest() {
    private val variableBinder = mock<TwoWayBooleanVariableBinder> {
        on { bindVariable(any(), any(), any(), any()) } doReturn mock()
    }

    private val captor = argumentCaptor<TwoWayVariableBinder.Callbacks<Boolean>>()
    private val underTest = DivSwitchBinder(baseBinder, variableBinder)
    private val path = DivStatePath(0)


    @Test
    fun `bind is_on_variable`() {
        val (divSwitch, view) = createDivAndView(SWITCH_WITH_ON_COLOR)
        underTest.bindView(bindingContext, view, divSwitch, path)

        verify(variableBinder).bindVariable(any(), eq(divSwitch.isOnVariable), any(), any())
        verifyNoMoreInteractions(variableBinder)
    }

    @Test
    fun `update isChecked after variable changed`() {
        val (divSwitch, view) = createDivAndView(SWITCH_WITH_ON_COLOR)
        underTest.bindView(bindingContext, view, divSwitch, path)
        verify(variableBinder).bindVariable(any(), any(), captor.capture(), any())

        val checked = true
        view.assertCheckedApplied(checked) {
            val callbacks = captor.allValues.single()
            callbacks.onVariableChanged(checked)
        }

        verifyNoMoreInteractions(variableBinder)
    }

    @Test
    fun `update variable after switch checked`() {
        val (divSwitch, view) = createDivAndView(SWITCH_WITH_ON_COLOR)
        val viewStateChangeListener = mock<(Boolean) -> Unit>()

        underTest.bindView(bindingContext, view, divSwitch, path)
        verify(variableBinder).bindVariable(any(), any(), captor.capture(), any())

        val checked = true

        view.assertCheckedApplied(checked) {
            val callbacks = captor.allValues.single()
            callbacks.setViewStateChangeListener(viewStateChangeListener)

            view.isChecked = checked
        }

        verify(viewStateChangeListener).invoke(checked)
        verifyNoMoreInteractions(viewStateChangeListener)

        verifyNoMoreInteractions(variableBinder)
    }

    @Test
    fun `on_color bound if provided`() {
        val (divSwitch, view) = createDivAndView(SWITCH_WITH_ON_COLOR)
        underTest.bindView(bindingContext, view, divSwitch, path)
        Assert.assertNotNull(view.colorOn)
        Assert.assertNotNull(view.thumbTintList)
        Assert.assertNotNull(view.trackTintList)
    }

    @Test
    fun `on_color not bound if not provided`() {
        val (divSwitch, view) = createDivAndView(SWITCH_WITHOUT_ON_COLOR)
        underTest.bindView(bindingContext, view, divSwitch, path)
        Assert.assertNull(view.colorOn)
        Assert.assertNotNull(view.thumbTintList)
        Assert.assertNotNull(view.trackTintList)
    }

    private fun DivSwitchView.assertCheckedApplied(expectedValue: Boolean, body: () -> Unit) {
        Assert.assertNotEquals(expectedValue, isChecked)
        body()
        Assert.assertEquals(expectedValue, isChecked)
    }

    private fun createDivAndView(jsonString: String): Pair<DivSwitch, DivSwitchView> {
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        val div = Div(environment, JSONObject(jsonString)) as Div.Switch
        val view = (viewCreator.create(div, ExpressionResolver.EMPTY) as DivSwitchView).apply {
            layoutParams = defaultLayoutParams()
        }
        return div.value to view
    }

    companion object {

        private val SWITCH_WITH_ON_COLOR = """
            {
              "type": "switch",
              "is_on_variable": "on_var",
              "on_color": "#ff0000",
              "width": {
                "type": "wrap_content"
              }
            }
        """.trimIndent()

        private val SWITCH_WITHOUT_ON_COLOR = """
            {
              "type": "switch",
              "is_on_variable": "on_var",
              "width": {
                "type": "wrap_content"
              }
            }
        """.trimIndent()
    }
}
