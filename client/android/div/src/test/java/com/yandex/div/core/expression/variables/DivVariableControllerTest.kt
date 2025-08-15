package com.yandex.div.core.expression.variables

import android.app.Activity
import android.os.Looper
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.data.VariableMutationException
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.lang.AssertionError
import org.mockito.stubbing.Answer

/**
 * Tests for [DivVariableController].
 */
@RunWith(RobolectricTestRunner::class)
class DivVariableControllerTest {
    private val internalVariableController = DivVariableController()
    private val underTest = DivVariableController(internalVariableController)
    private val parsingEnvironment = DivParsingEnvironment({ e -> throw AssertionError(e) })
    private val divJson = JSONObject("{\"log_id\":\"div2_sample_card\",\"states\":[{\"state_id\":0,\"div\":{\"type\":\"container\",\"items\":[]}}]}")

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
            on { onDeclared(any()) } doAnswer Answer { underTest.putOrUpdate(secondVar) }
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
    fun `nested variables are accessible via Div2View`() {
        val internalVariable = Variable.StringVariable("str_internal", "internal_value")
        internalVariableController.declare(internalVariable)

        val div2View = createDiv2View()

        val result = evaluateExpression(div2View, "@{${internalVariable.name}}")
        Assert.assertEquals(internalVariable.getValue(), result)
    }

    @Test
    fun `original variables are accessible via Div2View`() {
        val variable = Variable.StringVariable("str_original", "original_value")
        underTest.declare(variable)

        val div2View = createDiv2View()

        val result = evaluateExpression(div2View, "@{${variable.name}}")
        Assert.assertEquals(variable.getValue(), result)
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

    private fun evaluateExpression(div2View: Div2View, expression: String): Any {
        val actionWithInternalVariable = DivAction(
            parsingEnvironment,
            JSONObject("{\"url\":\"link\",\"log_id\":\"$expression\"}"))
        return actionWithInternalVariable.logId.evaluate(div2View.expressionResolver)
    }

    private fun createDiv2View(): Div2View {
        val divImageLoader = mock<DivImageLoader>()
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val div2Context = Div2Context(
            activity,
            DivConfiguration.Builder(divImageLoader)
                .divVariableController(underTest)
                .build()
        )

        val view = Div2View(div2Context)
        val data = DivData(parsingEnvironment, divJson)
        view.setData(data, DivDataTag("test"))
        return view
    }
}
