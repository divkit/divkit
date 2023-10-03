package com.yandex.div.core.expression.variables

import android.os.Looper
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [DivVariableController].
 */
@RunWith(RobolectricTestRunner::class)
class DivVariableControllerTest {
    private val internalVariableController = DivVariableController()
    private val underTest = DivVariableController(internalVariableController)

    @Test
    fun `putting variables with same name will update value of original one`() {
        val name = "str"
        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")

        underTest.putOrUpdate(firstVar)
        underTest.putOrUpdate(secondVar)

        val variableInSource = underTest.variableSource.getMutableVariable(name)!!
        Assert.assertEquals(firstVar, variableInSource)
        Assert.assertEquals(secondVar.defaultValue, variableInSource.getValue())
    }

    @Test
    fun `original variable will track change of variable which updated its value`() {
        val name = "str"
        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")

        underTest.putOrUpdate(firstVar)
        underTest.putOrUpdate(secondVar)

        val newValueVariable = Variable.StringVariable("unused", "C")
        secondVar.setValue(newValueVariable)

        val variableInSource = underTest.variableSource.getMutableVariable(name)!!
        Assert.assertEquals(firstVar, variableInSource)
        Assert.assertEquals(newValueVariable.defaultValue, variableInSource.getValue())
    }

    @Test
    fun `variable declaration notification happens only once`() {
        val name = "var_name"
        var lastVariableValue: Any? = null
        underTest.variableSource.observeDeclaration {
            lastVariableValue = it.getValue()
        }

        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")

        underTest.putOrUpdate(firstVar)
        underTest.putOrUpdate(secondVar)

        Assert.assertEquals("A", lastVariableValue)
    }

    @Test
    fun `variable declaration notifications happens after everything updated and declared`() {
        val firstVar = Variable.StringVariable("A", "value of A")
        val secondVar = Variable.StringVariable("B", "value of B")
        var secondVarAtDeclareTime: Variable? = null
        underTest.variableSource.observeDeclaration {
            secondVarAtDeclareTime = underTest.variableSource.getMutableVariable(secondVar.name)
        }

        underTest.putOrUpdate(firstVar, secondVar)
        Assert.assertEquals(secondVar, secondVarAtDeclareTime)
    }

    @Test
    fun `put or update in declaration callback not lead to deadlock`() = runBlocking {
        val firstVar = Variable.StringVariable("A", "value of A")
        val secondVar = Variable.StringVariable("B", "value of B")
        val declarationCallback = mock<(Variable) -> Unit> {
            on { invoke(any()) } doAnswer { underTest.putOrUpdate(secondVar) }
        }

        withContext(Dispatchers.IO) {
            Assert.assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
            underTest.variableSource.observeDeclaration(declarationCallback)
        }

        underTest.putOrUpdate(firstVar)
        verify(declarationCallback, times(2)).invoke(any())
    }

    @Test
    fun `request to defined and undefined variable is notified to observer`() {
        val observer = mock<(String) -> Unit>()
        underTest.putOrUpdate(Variable.StringVariable("Defined var", "value of A"))
        underTest.addVariableRequestObserver(observer)

        underTest.variableSource.getMutableVariable("Defined var")
        underTest.variableSource.getMutableVariable("Undefined var")

        verify(observer).invoke("Defined var")
        verify(observer).invoke("Undefined var")
    }

    @Test
    fun `variable declaration notification happens only once, second one throw exception`() {
        val name = "var_name"
        var lastVariableValue: Any? = null
        underTest.variableSource.observeDeclaration {
            lastVariableValue = it.getValue()
        }

        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")

        underTest.declare(firstVar)
        try {
            underTest.declare(secondVar)
        } catch (e: VariableDeclarationException) {
            Assert.assertEquals("A", lastVariableValue)
            return
        }

        Assert.fail("VariableDeclarationException is not thrown")
    }
    
    @Test
    fun `changing declared variable variable will change variable the variable source`() {
        val name = "str"
        val firstVar = Variable.StringVariable(name, "A")
        val secondVar = Variable.StringVariable(name, "B")
        underTest.declare(firstVar)

        val variable = underTest.get(name)
        variable?.setValue(secondVar) // change value of variable from "A" to "B"

        val variableInSource = underTest.variableSource.getMutableVariable(name)!!
        Assert.assertEquals(secondVar.defaultValue, variableInSource.getValue())
    }

    @Test
    fun `if variable not found in current controller, internal one will be checked`() {
        val name = "str_internal"
        val firstVar = Variable.StringVariable(name, "internal_value")
        internalVariableController.declare(firstVar)

        Assert.assertEquals("internal_value", underTest.get(name)?.getValue())
    }

    @Test
    fun `observing variables from current controller will also observe values declared in internal controller`() {
        val firstVar = Variable.StringVariable("strA", "A")
        val secondVar = Variable.StringVariable("strB", "B")
        underTest.declare(firstVar)
        internalVariableController.declare(secondVar)

        val observer = mock<(String) -> Unit>()
        underTest.addVariableRequestObserver(observer)

        underTest.variableSource.getMutableVariable(firstVar.name)
        internalVariableController.variableSource.getMutableVariable(secondVar.name)

        verify(observer).invoke(firstVar.name)
        verify(observer).invoke(secondVar.name)
    }
}
