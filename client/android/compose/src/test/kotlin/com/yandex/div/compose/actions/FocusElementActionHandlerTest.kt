package com.yandex.div.compose.actions

import androidx.compose.ui.focus.FocusRequester
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.focusElementAction
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class FocusElementActionHandlerTest {
    private val environment = ActionHandlerEnvironment()
    private val storage = FocusBindingStorage()
    private val handler = FocusElementActionHandler(storage, environment.reporter)
    private val requester = mock<FocusRequester>()

    @BeforeTest
    fun setUp() {
        environment.init(
            focusElementActionHandler = handler
        )
    }

    @Test
    fun `element id is evaluated again for each action`() {
        val target = Variable.StringVariable("target", "first")
        environment.variableController.declare(target)
        val second = mock<FocusRequester>()
        storage.register("first", requester)
        storage.register("second", second)
        val action = action(typed = focusElementAction(expression("@{target}")))

        environment.handle(action)
        target.setValueDirectly("second")
        environment.handle(action)

        verify(requester).requestFocus()
        verify(second).requestFocus()
    }

    @Test
    fun `element id uses the action context`() {
        storage.register("local", requester)
        environment.variableController.declare(Variable.StringVariable("target", "root"))
        val local = ActionHandlerEnvironment()
        local.variableController.declare(Variable.StringVariable("target", "local"))

        environment.actionHandler.handle(
            context = local.context,
            action = action(typed = focusElementAction(expression("@{target}"))),
            source = DivActionSource.TAP,
        )

        verify(requester).requestFocus()
    }

    @Test
    fun `disabled action does not evaluate element id`() {
        environment.handle(action(isEnabled = false, typed = focusElementAction(expression("@{missing}"))))

        assertEquals(emptyList(), environment.reporter.errors)
    }

    @Test
    fun `missing target reports the requested id`() {
        environment.reporter.failOnError = false

        environment.handle(action(typed = focusElementAction("missing")))

        assertEquals(listOf("Element with id 'missing' not found for focus_element action"),
            environment.reporter.errors)
    }
}
