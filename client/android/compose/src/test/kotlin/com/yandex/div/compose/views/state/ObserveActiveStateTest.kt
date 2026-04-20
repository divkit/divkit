package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.expression
import com.yandex.div2.DivState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class ObserveActiveStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController
    )

    @Test
    fun `resolves state by stateIdVariable`() {
        val stateVar = Variable.StringVariable("current_state", "second")
        variableController.declare(stateVar)

        val divState = divState(
            stateIdVariable = "current_state",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `fallback to defaultStateId when no variable`() {
        val divState = divState(
            defaultStateId = constant("second"),
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `fallback to first state when nothing specified`() {
        val divState = divState(
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertEquals("first", result?.stateId)
    }

    @Test
    fun `variable change triggers state switch`() {
        val stateVar = Variable.StringVariable("current_state", "first")
        variableController.declare(stateVar)

        val divState = divState(
            stateIdVariable = "current_state",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertEquals("first", result?.stateId)

        stateVar.set("second")
        composeRule.waitForIdle()

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `defaultStateId is not reactive`() {
        val counterVar = Variable.StringVariable("default_id", "first")
        variableController.declare(counterVar)

        val divState = divState(
            defaultStateId = expression("@{default_id}"),
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertEquals("first", result?.stateId)

        counterVar.set("second")
        composeRule.waitForIdle()

        assertEquals("first", result?.stateId)
    }

    @Test
    fun `reports warning when stateIdVariable is absent`() {
        val divState = divState(
            defaultStateId = constant("first"),
            states = listOf(DivState.State(stateId = "first")),
        )

        setContent { divState.observeActiveState() }

        assertEquals(
            "div-state: state switching is not supported without state_id_variable",
            reporter.lastWarning,
        )
    }

    @Test
    fun `reports error for unknown state id`() {
        reporter.failOnError = false
        variableController.declare(Variable.StringVariable("s", "missing"))

        val divState = divState(
            stateIdVariable = "s",
            states = listOf(DivState.State(stateId = "first")),
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { result = divState.observeActiveState() }

        assertNotNull(reporter.lastError)
        assertEquals("first", result?.stateId)
    }

    private fun divState(
        states: List<DivState.State>,
        defaultStateId: Expression<String>? = null,
        stateIdVariable: String? = null,
    ) = DivState(
        states = states,
        defaultStateId = defaultStateId,
        stateIdVariable = stateIdVariable,
    )

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
