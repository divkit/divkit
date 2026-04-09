package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.context.DivLocalContext
import com.yandex.div.compose.context.LocalDivContext
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class DivVariableControllerTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val variableController = DivVariableController()
    private val reporter = TestReporter()

    private val localContext = DivLocalContext(
        actionHandlingContext = mock(),
        expressionResolver = createExpressionResolver(
            reporter = reporter,
            variableController = variableController
        ),
        functionProvider = mock(),
        triggerStorage = mock(),
        variableController = variableController
    )

    @Test
    fun `returns current value of string variable`() {
        variableController.declare(Variable.StringVariable("state", "active"))
        var result by mutableStateOf<String?>(null)

        setContent {
            result = variableController.observedVariableValue("state")
        }

        assertEquals("active", result)
    }

    @Test
    fun `returns null for nonexistent variable`() {
        reporter.failOnError = false
        var result by mutableStateOf<String?>("initial")

        setContent {
            result = variableController.observedVariableValue("nonexistent")
        }

        assertNull(result)
    }

    @Test
    fun `reports error for nonexistent variable`() {
        reporter.failOnError = false

        setContent {
            variableController.observedVariableValue("missing_var")
        }

        assertEquals("variable [missing_var] not found", reporter.lastError)
    }

    @Test
    fun `returns null for non-string variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.IntegerVariable("count", 42))
        var result by mutableStateOf<String?>("initial")

        setContent {
            result = variableController.observedVariableValue("count")
        }

        assertNull(result)
    }

    @Test
    fun `reports error for non-string variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.BooleanVariable("flag", true))

        setContent {
            variableController.observedVariableValue("flag")
        }

        assertEquals("variable [flag] is not a string variable", reporter.lastError)
    }

    @Test
    fun `updates when variable value changes`() {
        val variable = Variable.StringVariable("state", "first")
        variableController.declare(variable)
        var result by mutableStateOf<String?>(null)

        setContent {
            result = variableController.observedVariableValue("state")
        }

        assertEquals("first", result)

        variable.set("second")
        composeRule.waitForIdle()

        assertEquals("second", result)
    }

    @Test
    fun `stops observing when leaving composition`() {
        val variable = Variable.StringVariable("state", "initial")
        variableController.declare(variable)
        var show by mutableStateOf(true)
        var result by mutableStateOf<String?>(null)

        setContent {
            if (show) {
                result = variableController.observedVariableValue("state")
            }
        }

        assertEquals("initial", result)

        show = false
        composeRule.waitForIdle()

        variable.set("updated")
        composeRule.waitForIdle()

        assertEquals("initial", result)
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            val divContext = DivContext(
                baseContext = LocalContext.current,
                configuration = DivComposeConfiguration(
                    reporter = reporter,
                    variableController = variableController
                )
            )
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalDivContext provides localContext,
            ) {
                content()
            }
        }
    }
}
