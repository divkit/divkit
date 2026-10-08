package com.yandex.div.core.expression.variables

import android.os.Looper
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.data.VariableMutationException
import com.yandex.div.internal.variables.DeclarationObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
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

        underTest.variableSource.observeDeclaration (object : DeclarationObserver {
            override fun onDeclared(variable: Variable) {
                lastVariableValue = variable.getValue()
            }
            override fun onUndeclared(variable: Variable) = Unit
        })

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
        underTest.variableSource.observeDeclaration (object : DeclarationObserver {
            override fun onDeclared(variable: Variable) {
                secondVarAtDeclareTime = underTest.variableSource.getMutableVariable(secondVar.name)
            }
            override fun onUndeclared(variable: Variable) = Unit
        })

        underTest.putOrUpdate(firstVar, secondVar)
        Assert.assertEquals(secondVar, secondVarAtDeclareTime)
    }

    @Test
    fun `put or update in declaration callback not lead to deadlock`() = runBlocking {
        val firstVar = Variable.StringVariable("A", "value of A")
        val secondVar = Variable.StringVariable("B", "value of B")
        val declarationCallback = mock<DeclarationObserver> {
            on { onDeclared(any()) } doAnswer { underTest.putOrUpdate(secondVar) }
        }

        withContext(Dispatchers.IO) {
            Assert.assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
            underTest.variableSource.observeDeclaration(declarationCallback)
        }

        underTest.putOrUpdate(firstVar)
        verify(declarationCallback, times(2)).onDeclared(any())
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
        val declarationCallback = object : DeclarationObserver {
            override fun onDeclared(variable: Variable) { lastVariableValue = variable.getValue() }
            override fun onUndeclared(variable: Variable) = Unit
        }
        underTest.variableSource.observeDeclaration(declarationCallback)

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

    @Test
    fun `removing variable removes only provided variables `() {
        val strVariable = Variable.StringVariable("str_original", "original_value")
        val strVariable2 = Variable.StringVariable("str_original2", "original_value")
        val strVariable3 = Variable.StringVariable("str_original3", "original_value")
        underTest.declare(strVariable, strVariable2, strVariable3)

        underTest.removeAll("str_original")

        Assert.assertNotNull(underTest.get("str_original2"))
        Assert.assertNotNull(underTest.get("str_original3"))
    }

    @Test
    fun `after removing variable and redeclaration of variable with the same name only new one will be used`() {
        val original = Variable.StringVariable("name", "original_value")
        val redeclared = Variable.StringVariable("name", "redeclared_value")

        underTest.declare(original)
        Assert.assertEquals("original_value", underTest.get("name")?.getValue())

        underTest.removeAll("name")
        Assert.assertNull(underTest.get("name"))

        underTest.putOrUpdate(redeclared)
        Assert.assertEquals("redeclared_value", underTest.get("name")?.getValue())

        // changing removed variable should not cause any change
        original.setValue(Variable.StringVariable("name", "updated_original"))
        Assert.assertEquals("redeclared_value", underTest.get("name")?.getValue())

        // changing redeclared variable should update value in DivVariableController
        redeclared.setValue(Variable.StringVariable("name", "updated_value"))
        Assert.assertEquals("updated_value", underTest.get("name")?.getValue())
    }

    @Test
    fun `replacing variable with a variable of other type throws an exception`() {
        val strVariable = Variable.StringVariable("str_original", "original_value")
        underTest.declare(strVariable)

        val doubleVariable = Variable.DoubleVariable("str_original", 1.0)
        try {
            underTest.replaceAll(doubleVariable)
            Assert.fail()
        } catch (e: VariableMutationException) { }
    }

    @Test
    fun `removing variable and putting variable with another type with the same name throws an exception`() {
        val strVariable = Variable.StringVariable("str_original", "original_value")
        underTest.declare(strVariable)

        underTest.removeAll("str_original")

        val doubleVariable = Variable.DoubleVariable("str_original", 1.0)
        try {
            underTest.putOrUpdate(doubleVariable)
            Assert.fail()
        } catch (e: VariableMutationException) { }
    }

    @Test
    fun `declaring a variable and putting the same variable`() {
        var count = 0
        val strVariable = mock<Variable.StringVariable> {
            on { name } doReturn "str_original"
            on { addObserver(any()) } doAnswer {
                count++
                Unit
            }
        }

        strVariable.addObserver {  }

        underTest.declare(strVariable)
        underTest.putOrUpdate(strVariable)
        underTest.putOrUpdate(strVariable)

        strVariable.set("new_val")

        Assert.assertEquals(1, count)
    }

    @Test
    fun `get returns null for undeclared variable`() {
        Assert.assertNull(underTest.get("unknown"))
    }

    @Test
    fun `isDeclared is true for local and internal variables only`() {
        underTest.declare(Variable.StringVariable("local", "A"))
        internalVariableController.declare(Variable.StringVariable("internal", "B"))

        Assert.assertTrue(underTest.isDeclared("local"))
        Assert.assertTrue(underTest.isDeclared("internal"))
        Assert.assertFalse(underTest.isDeclared("unknown"))
        Assert.assertFalse(internalVariableController.isDeclared("local"))
    }

    @Test
    fun `isDeclared is false after variable removal`() {
        underTest.declare(Variable.StringVariable("name", "A"))

        underTest.removeAll("name")

        Assert.assertFalse(underTest.isDeclared("name"))
    }

    @Test
    fun `declaring already declared variable does not change existing value`() {
        underTest.declare(Variable.StringVariable("name", "A"))

        try {
            underTest.declare(Variable.StringVariable("name", "B"))
            Assert.fail("VariableDeclarationException is not thrown")
        } catch (e: VariableDeclarationException) { }

        Assert.assertEquals("A", underTest.get("name")?.getValue())
    }

    @Test
    fun `declaring several variables fails entirely if one of them is already declared`() {
        underTest.declare(Variable.StringVariable("existing", "A"))

        try {
            underTest.declare(
                Variable.StringVariable("new", "B"),
                Variable.StringVariable("existing", "C")
            )
            Assert.fail("VariableDeclarationException is not thrown")
        } catch (e: VariableDeclarationException) { }

        Assert.assertNull(underTest.get("new"))
        Assert.assertEquals("A", underTest.get("existing")?.getValue())
    }

    @Test
    fun `declare allows to declare several variables at once`() {
        underTest.declare(
            Variable.StringVariable("a", "A"),
            Variable.IntegerVariable("b", 2)
        )

        Assert.assertEquals("A", underTest.get("a")?.getValue())
        Assert.assertEquals(2L, underTest.get("b")?.getValue())
    }

    @Test
    fun `variable declared in internal controller can be redeclared locally`() {
        internalVariableController.declare(Variable.StringVariable("name", "internal"))

        underTest.declare(Variable.StringVariable("name", "local"))

        Assert.assertEquals("local", underTest.get("name")?.getValue())
        Assert.assertEquals("internal", internalVariableController.get("name")?.getValue())
    }

    @Test
    fun `replaceAll removes variables which are not provided`() {
        underTest.declare(
            Variable.StringVariable("kept", "A"),
            Variable.StringVariable("dropped", "B")
        )

        underTest.replaceAll(
            Variable.StringVariable("kept", "A2"),
            Variable.StringVariable("added", "C")
        )

        Assert.assertEquals("A2", underTest.get("kept")?.getValue())
        Assert.assertEquals("C", underTest.get("added")?.getValue())
        Assert.assertNull(underTest.get("dropped"))
        Assert.assertFalse(underTest.isDeclared("dropped"))
    }

    @Test
    fun `replaceAll does not affect internal controller`() {
        internalVariableController.declare(Variable.StringVariable("internal", "A"))

        underTest.replaceAll(Variable.StringVariable("local", "B"))

        Assert.assertEquals("A", underTest.get("internal")?.getValue())
    }

    @Test
    fun `removeAll does not affect internal controller`() {
        internalVariableController.declare(Variable.StringVariable("internal", "A"))

        underTest.removeAll("internal")

        Assert.assertEquals("A", underTest.get("internal")?.getValue())
    }

    @Test
    fun `removing variable notifies declaration observer about undeclaration`() {
        val variable = Variable.StringVariable("name", "A")
        underTest.declare(variable)
        val observer = mock<DeclarationObserver>()
        underTest.variableSource.observeDeclaration(observer)

        underTest.removeAll("name")

        verify(observer).onUndeclared(variable)
    }

    @Test
    fun `captureAllVariables returns local and internal variables`() {
        val local = Variable.StringVariable("local", "A")
        val internal = Variable.StringVariable("internal", "B")
        underTest.declare(local)
        internalVariableController.declare(internal)

        val captured = underTest.captureAllVariables()

        Assert.assertEquals(setOf(local, internal), captured.toSet())
    }

    @Test
    fun `removed request observer is not notified`() {
        val observer = mock<(String) -> Unit>()
        underTest.addVariableRequestObserver(observer)
        underTest.removeVariableRequestObserver(observer)

        underTest.variableSource.getMutableVariable("name")

        verify(observer, never()).invoke(any())
    }
}
