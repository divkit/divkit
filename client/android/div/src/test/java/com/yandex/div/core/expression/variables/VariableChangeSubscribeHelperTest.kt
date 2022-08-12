package com.yandex.div.core.expression.variables

import android.os.Looper
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class VariableChangeSubscribeHelperTest {

    private val errorCollector = mock<ErrorCollector>()
    private val declarationNotifier = mock<VariableDeclarationNotifier>()
    private val variableController = mock<VariableController> {
        on { declarationNotifier } doReturn declarationNotifier
    }

    @Test
    fun `change of variable will be triggered after variable gets declared`() {
        var lastValue: String? = "non-initialized"
        val variable = Variable.StringVariable("late_init_variable", "declared_value")
        subscribe(variable.name) { value -> lastValue = value }

        declareVariable(variable)

        Assert.assertEquals("declared_value", lastValue)
    }

    @Test
    fun `callback is invoked on subscription when it must be invoked`() {
        var lastValue: String? = "non-initialized"
        val variable = Variable.StringVariable("late_init_variable", "declared_value")
        whenever(variableController.getMutableVariable(variable.name)).thenReturn(variable)

        subscribe(variable.name, true) { value -> lastValue = value }

        Assert.assertEquals("declared_value", lastValue)
    }

    @Test
    fun `callback is not invoked on subscription when it must not be invoked`() {
        var lastValue: String? = "non-initialized"
        val variable = Variable.StringVariable("late_init_variable", "declared_value")
        whenever(variableController.getMutableVariable(variable.name)).thenReturn(variable)

        subscribe(variable.name) { value -> lastValue = value }

        Assert.assertEquals("non-initialized", lastValue)
    }

    @Test
    fun `callback is invoked on main thread even if subscribe from bg`() = runBlocking {
        var lastValue: String? = "non-initialized"
        val variable = Variable.StringVariable("late_init_variable", "declared_value")
        whenever(variableController.getMutableVariable(variable.name)).thenReturn(variable)

        withContext(Dispatchers.IO) {
            subscribe(variable.name, true) { value ->
                Assert.assertEquals(Looper.getMainLooper(), Looper.myLooper())
                lastValue = value
            }
        }

        shadowOf(Looper.getMainLooper()).idle()
        Assert.assertEquals("declared_value", lastValue)
    }

    private fun subscribe(
        variableName: String,
        invokeChangeOnSubscription: Boolean = false,
        callback: (String?) -> Unit
    ) = subscribeToVariable(variableName,
            errorCollector,
            variableController,
            invokeChangeOnSubscription,
            callback
        )

    private fun declareVariable(variable: Variable) {
        whenever(variableController.getMutableVariable(variable.name)).thenReturn(variable)
        val action = argumentCaptor<(Variable) -> Unit>()
        verify(declarationNotifier).doOnVariableDeclared(eq(variable.name), action.capture())
        action.allValues.forEach { it.invoke(variable) }
    }
}
