package com.yandex.div.core.expression.variables


import com.yandex.div.data.Variable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
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

    private val declareVariableObserver = argumentCaptor<(Variable) -> Unit>()
    private val source = mock<VariableSource> {
        on { observeDeclaration(declareVariableObserver.capture()) } doAnswer {}
    }

    private val underTest = VariableController().apply {
        localVariables.forEach { v -> declare(v) }
        addSource(source)
    }

    @Test
    fun `null returned when no variable specified`() {
        Assert.assertNull(underTest.getMutableVariable("unknown_variable"))
    }

    @Test
    fun `resolving string variable`() {
        Assert.assertEquals("lorem ipsum", underTest.getMutableVariable("some_text")!!.getValue())
    }

    @Test
    fun `resolving numeric variable`() {
        Assert.assertEquals(0, underTest.getMutableVariable("zero")!!.getValue())
    }

    @Test
    fun `variable declaration notification happens only once`() {
        val name = "var_name"
        var lastVariableValue: Any? = null
        underTest.declarationNotifier.doOnVariableDeclared(name) {
            lastVariableValue = it.getValue()
        }

        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")

        declareVariableObserver.firstValue.invoke(firstVar)
        declareVariableObserver.firstValue.invoke(secondVar)

        Assert.assertEquals("A", lastVariableValue)
    }
}
