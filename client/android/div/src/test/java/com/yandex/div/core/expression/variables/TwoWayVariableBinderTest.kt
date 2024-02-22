package com.yandex.div.core.expression.variables

import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.ExpressionsRuntimeProvider
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
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

    private val errorCollector = mock<ErrorCollector>()
    private val errorCollectors = mock<ErrorCollectors> {
        on { getOrCreate(any(), any()) } doReturn errorCollector
    }
    private val variable = Variable.StringVariable(VARIABLE_NAME, INITIAL_VALUE)
    private val variableController = VariableController().apply {
        declare(variable)
    }
    private val expressionsRuntime = ExpressionsRuntime(mock(), variableController, mock())
    private val expressionsRuntimeProvider = mock<ExpressionsRuntimeProvider> {
        on { getOrCreate(any(), any(), any()) } doReturn expressionsRuntime
    }

    private val divView = mock<Div2View> {
        on { dataTag } doReturn mock()
        on { divData } doReturn mock()
    }
    private val updateCaptor = argumentCaptor<(String) -> Unit>()
    private val callbacks = mock<TwoWayStringVariableBinder.Callbacks> {
        on { setViewStateChangeListener(updateCaptor.capture()) } doAnswer {}
    }

    init {
        TwoWayStringVariableBinder(errorCollectors, expressionsRuntimeProvider)
            .bindVariable(divView, VARIABLE_NAME, callbacks)
    }

    @Test
    fun `invoke callback on subscribe`() {
        verify(callbacks).onVariableChanged(INITIAL_VALUE)
    }

    @Test
    fun `invoke callback on variable change`() {
        variable.set(NEW_VALUE)
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
        variable.set(NEW_VALUE)
        verify(callbacks, never()).onVariableChanged(NEW_VALUE)
    }

    @Test
    fun `not set variable value on view state change after variable change with same value`() {
        variable.set(NEW_VALUE)
        val variableObserver = mock<(Variable) -> Unit>()
        variable.addObserver(variableObserver)

        updateCaptor.firstValue.invoke(NEW_VALUE)

        verify(variableObserver, never()).invoke(variable)
    }

    @Test
    fun `invoke callback on variable change after view state change with another value`() {
        updateCaptor.firstValue.invoke(NEW_VALUE)
        variable.set(ANOTHER_VALUE)
        verify(callbacks).onVariableChanged(ANOTHER_VALUE)
    }

    @Test
    fun `set variable value on view state change after variable change with another value`() {
        variable.set(NEW_VALUE)
        updateCaptor.firstValue.invoke(ANOTHER_VALUE)
        assertEquals(ANOTHER_VALUE, variable.getValue())
    }
}
