package com.yandex.div.core.expression.variables


import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [VariableController].
 */
@RunWith(RobolectricTestRunner::class)
class VariableControllerTest {
    private val localVariables = listOf(
        mock<Variable.IntegerVariable> {
            on { name } doReturn "zero"
            on { getValue() } doReturn 0
        },
        mock<Variable.StringVariable> {
            on { name } doReturn "some_text"
            on { getValue() } doReturn "lorem ipsum"
        },
    )

    private val errorCollector = mock<ErrorCollector>()
    private lateinit var globalVariableController : GlobalVariableController
    private lateinit var variableController : VariableController

    @Before
    fun initVariableController() {
        globalVariableController = GlobalVariableController()
        variableController = VariableController().apply {
            localVariables.forEach {
                declare(it)
            }
            addSource(globalVariableController.variableSource)
        }
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
        declareVariable(variable)
        subscribe(variable.name, true) { value -> lastValue = value }

        Assert.assertEquals("declared_value", lastValue)
    }

    @Test
    fun `callback is not invoked on subscription when it must not be invoked`() {
        var lastValue: String? = "non-initialized"
        val variable = Variable.StringVariable("late_init_variable", "declared_value")
        declareVariable(variable)

        subscribe(variable.name) { value -> lastValue = value }

        Assert.assertEquals("non-initialized", lastValue)
    }

    @Test
    fun `null returned when no variable specified`() {
        Assert.assertNull(variableController.getMutableVariable("unknown_variable"))
    }

    @Test
    fun `resolving string variable`() {
        Assert.assertEquals("lorem ipsum", variableController.getMutableVariable("some_text")!!.getValue())
    }

    @Test
    fun `resolving numeric variable`() {
        Assert.assertEquals(0, variableController.getMutableVariable("zero")!!.getValue())
    }

    private fun subscribe(
        variableName: String,
        invokeChangeOnSubscription: Boolean = false,
        callback: (String?) -> Unit
    ) = variableController.subscribeToVariableChange(variableName, errorCollector, invokeChangeOnSubscription) {
        callback(it.getValue() as String?)
    }

    private fun declareVariable(variable: Variable) {
        globalVariableController.declare(variable)
    }
}
