package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div2.DivData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

private const val VARIABLE_NAME = "variable_name"
private const val INITIAL_VALUE = "initial_value"
private const val NEW_VALUE = "new_value"
private const val ANOTHER_VALUE = "another_value"

@RunWith(RobolectricTestRunner::class)
class TwoWayVariableBinderTest {

    private val variable = Variable.StringVariable(VARIABLE_NAME, INITIAL_VALUE)
    private val variableUpdateCaptor = argumentCaptor<(Variable?) -> Unit>()
    private val invokeOnSubscriptionCaptor = argumentCaptor<Boolean>()
    private val variableController = mock<VariableController> {
        on { getMutableVariable(VARIABLE_NAME) } doReturn variable
        on {
            subscribeToVariableChange(
                any(),
                anyOrNull(),
                invokeOnSubscriptionCaptor.capture(),
                variableUpdateCaptor.capture()
            )
        } doAnswer {
            if (invokeOnSubscriptionCaptor.firstValue) {
                variableUpdateCaptor.firstValue.invoke(variable)
            }
            Disposable.NULL
        }
    }
    private val expressionResolver = mock<ExpressionResolverImpl> {
        on { variableController } doReturn variableController
    }

    private val divView = mock<Div2View> {
        on { dataTag } doReturn mock()
        on { divData } doReturn DivData(logId = "test", states = emptyList())
    }
    private val updateCaptor = argumentCaptor<(String) -> Unit>()
    private val callbacks = mock<TwoWayStringVariableBinder.Callbacks> {
        on { setViewStateChangeListener(updateCaptor.capture()) } doAnswer {}
    }

    init {
        val bindingContext = BindingContext.createEmpty(divView).getFor(expressionResolver)
        TwoWayStringVariableBinder(mock()).bindVariable(bindingContext, VARIABLE_NAME, callbacks, mock())
    }

    @Test
    fun `invoke callback on subscribe`() {
        verify(callbacks).onVariableChanged(INITIAL_VALUE)
    }

    @Test
    fun `invoke callback on variable change`() {
        updateVariable()
        verify(callbacks).onVariableChanged(NEW_VALUE)
    }

    @Test
    fun `set variable value on view state change`() {
        updateCaptor.firstValue.invoke(NEW_VALUE)
        assertEquals(NEW_VALUE, variable.getValue())
    }

    @Test
    fun `not invoke callback on variable change after view state change with same value`() {
        updateCaptor.firstValue.invoke(NEW_VALUE)
        updateVariable()
        verify(callbacks, never()).onVariableChanged(NEW_VALUE)
    }

    @Test
    fun `not set variable value on view state change after variable change with same value`() {
        updateVariable()
        val variableObserver = mock<(Variable) -> Unit>()
        variable.addObserver(variableObserver)

        updateCaptor.firstValue.invoke(NEW_VALUE)

        verify(variableObserver, never()).invoke(variable)
    }

    @Test
    fun `invoke callback on variable change after view state change with another value`() {
        updateCaptor.firstValue.invoke(NEW_VALUE)
        updateVariable(ANOTHER_VALUE)
        verify(callbacks).onVariableChanged(ANOTHER_VALUE)
    }

    @Test
    fun `set variable value on view state change after variable change with another value`() {
        updateVariable()
        updateCaptor.firstValue.invoke(ANOTHER_VALUE)
        assertEquals(ANOTHER_VALUE, variable.getValue())
    }

    private fun updateVariable(value: String = NEW_VALUE) {
        variable.set(value)
        variableUpdateCaptor.firstValue.invoke(variable)
    }
}
