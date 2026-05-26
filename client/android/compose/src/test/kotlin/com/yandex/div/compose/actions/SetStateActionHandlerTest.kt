package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.setStateAction
import com.yandex.div2.DivAction
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class SetStateActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val stateStorage = DivStateStorage()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    private val rootPath = DivStatePath.fromState(0L)

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            setStateActionHandler = SetStateActionHandler(
                reporter = reporter,
                stateStorage = stateStorage
            )
        )
    }

    @Test
    fun `typed action sets active state`() {
        handle(action(typed = setStateAction("0/menu/main")))

        assertEquals("main", stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `typed action sets nested active state`() {
        handle(action(typed = setStateAction("0/menu/main/footer/compact")))

        val parentPath = DivStatePath.parse("0/menu/main")
        assertEquals("compact", stateStorage.getActiveState(parentPath, "footer"))
    }

    @Test
    fun `typed action overrides previously set state`() {
        handle(action(typed = setStateAction("0/menu/main")))
        handle(action(typed = setStateAction("0/menu/details")))

        assertEquals("details", stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `typed action evaluates stateId expression`() {
        variableController.declare(Variable.StringVariable("target", "main"))

        handle(action(typed = setStateAction(expression("0/menu/@{target}"))))

        assertEquals("main", stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `div-action sets active state`() {
        handle(action(url = "div-action://set_state?state_id=0/menu/main"))

        assertEquals("main", stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `div-action with temporary=false sets active state`() {
        handle(action(url = "div-action://set_state?state_id=0/menu/main&temporary=false"))

        assertEquals("main", stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `typed action notifies bound variable setter`() {
        val received = mutableListOf<String>()
        stateStorage.bindStateIdVariable(rootPath, "menu") { received.add(it) }

        handle(action(typed = setStateAction("0/menu/main")))

        assertEquals(listOf("main"), received)
    }

    @Test
    fun `error on malformed path - non-numeric top level`() {
        reporter.failOnError = false

        handle(action(typed = setStateAction("abc/menu/main")))

        assertEquals("Invalid set_state path: abc/menu/main", reporter.lastError)
        assertNull(stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `error on malformed path - even number of segments`() {
        reporter.failOnError = false

        handle(action(typed = setStateAction("0/menu")))

        assertEquals("Invalid set_state path: 0/menu", reporter.lastError)
        assertNull(stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `error on path with no state`() {
        reporter.failOnError = false

        handle(action(typed = setStateAction("0")))

        assertEquals("Missing state in set_state path: 0", reporter.lastError)
    }

    @Test
    fun `error on path with empty state id`() {
        reporter.failOnError = false

        handle(action(typed = setStateAction("0/menu/")))

        assertEquals("Empty state id in set_state path: 0/menu/", reporter.lastError)
        assertNull(stateStorage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `div-action error on malformed path`() {
        reporter.failOnError = false

        handle(action(url = "div-action://set_state?state_id=abc"))

        assertEquals("Invalid set_state path: abc", reporter.lastError)
    }

    @Test
    fun `div-action error on path with no state`() {
        reporter.failOnError = false

        handle(action(url = "div-action://set_state?state_id=0"))

        assertEquals("Missing state in set_state path: 0", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}
