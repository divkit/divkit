package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
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
        on { getVariable(VARIABLE_NAME) } doReturn variable
    }

    private val variableMutationHandler = mock<VariableMutationHandler>()
    private val errorCollector = mock<ErrorCollector>()
    private val updateCaptor = argumentCaptor<(String) -> Unit>()
    private val callbacks = mock<TwoWayStringVariableBinder.Callbacks> {
        on { setViewStateChangeListener(updateCaptor.capture()) } doAnswer {}
    }

    init {
        TwoWayStringVariableBinder(variableMutationHandler)
            .bindVariable(VARIABLE_NAME, expressionResolver, errorCollector, callbacks)
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
        verify(variableMutationHandler).setVariable(eq(VARIABLE_NAME), eq(NEW_VALUE), any(), any())
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
        verify(variableMutationHandler).setVariable(eq(VARIABLE_NAME), eq(ANOTHER_VALUE), any(), any())
    }

    private fun updateVariable(value: String = NEW_VALUE) {
        variable.set(value)
        variableUpdateCaptor.firstValue.invoke(variable)
    }
}
