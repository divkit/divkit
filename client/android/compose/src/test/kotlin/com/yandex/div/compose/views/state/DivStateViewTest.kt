package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.mockViewContext
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.state.WithRootStatePath
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.state
import com.yandex.div2.DivState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class DivStateViewTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val storage = DivStateStorage()

    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController
    )

    private val viewContext = mockViewContext(stateStorage = storage)

    @Test
    fun `resolves state by stateIdVariable`() {
        val stateVar = Variable.StringVariable("current_state", "second")
        variableController.declare(stateVar)

        val divState = state(
            id = "menu",
            stateIdVariable = "current_state",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `fallback to defaultStateId when no variable`() {
        val divState = state(
            id = "menu",
            defaultStateId = constant("second"),
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `fallback to first state when nothing specified`() {
        val divState = state(
            id = "menu",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("first", result?.stateId)
    }

    @Test
    fun `variable change triggers state switch`() {
        val stateVar = Variable.StringVariable("current_state", "first")
        variableController.declare(stateVar)

        val divState = state(
            id = "menu",
            stateIdVariable = "current_state",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("first", result?.stateId)

        stateVar.set("second")
        composeRule.waitForIdle()

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `defaultStateId is not reactive`() {
        val counterVar = Variable.StringVariable("default_id", "first")
        variableController.declare(counterVar)

        val divState = state(
            id = "menu",
            defaultStateId = expression("@{default_id}"),
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("first", result?.stateId)

        counterVar.set("second")
        composeRule.waitForIdle()

        assertEquals("first", result?.stateId)
    }

    @Test
    fun `reports error for unknown state id`() {
        reporter.failOnError = false
        variableController.declare(Variable.StringVariable("s", "missing"))

        val divState = state(
            id = "menu",
            stateIdVariable = "s",
            states = listOf(DivState.State(stateId = "first")),
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("State with id 'missing' not found", reporter.lastError)
        assertEquals("first", result?.stateId)
    }

    @Test
    fun `reports error for empty states array`() {
        reporter.failOnError = false

        val divState = state(id = "menu", stateIdVariable = "s", states = listOf())

        setContent { WithDivState(divState) { } }

        assertEquals("DivState has no states", reporter.lastError)
    }

    @Test
    fun `reports error when DivState id is missing`() {
        reporter.failOnError = false

        val divState = state(
            states = listOf(DivState.State(stateId = "first")),
        )

        setContent { WithDivState(divState) { } }

        assertEquals("DivState id is missing at path '0'", reporter.lastError)
    }

    @Test
    fun `reads active state from storage when id is set and no variable`() {
        storage.setActiveState(DivStatePath.parse("0/menu/second"))

        val divState = state(
            id = "menu",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `setActiveState triggers recomposition`() {
        val divState = state(
            id = "menu",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        var result by mutableStateOf<DivState.State?>(null)
        setContent { WithDivState(divState) { result = it } }

        assertEquals("first", result?.stateId)

        storage.setActiveState(DivStatePath.parse("0/menu/second"))
        composeRule.waitForIdle()

        assertEquals("second", result?.stateId)
    }

    @Test
    fun `setActiveState syncs bound stateIdVariable`() {
        val stateVar = Variable.StringVariable("current_state", "first")
        variableController.declare(stateVar)

        val divState = state(
            id = "menu",
            stateIdVariable = "current_state",
            states = listOf(
                DivState.State(stateId = "first"),
                DivState.State(stateId = "second"),
            )
        )

        setContent { WithDivState(divState) { } }

        storage.setActiveState(DivStatePath.parse("0/menu/second"))
        composeRule.waitForIdle()

        assertEquals("second", stateVar.getValue())
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalComponent provides localComponent,
                LocalDivViewContext provides viewContext,
            ) {
                WithRootStatePath(rootStateId = 0L) {
                    content()
                }
            }
        }
    }
}
