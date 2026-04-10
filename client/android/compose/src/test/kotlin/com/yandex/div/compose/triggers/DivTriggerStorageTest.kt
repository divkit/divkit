package com.yandex.div.compose.triggers

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.trigger
import com.yandex.div2.DivTrigger.Mode
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTriggerStorageTest {

    private val actionHandler = mock<DivActionHandler>()
    private val actionHandlingContext = mock<DivActionHandlingContext>()
    private val variableController = DivVariableController()

    private val triggerStorage = DivTriggerStorage(
        actionHandler = actionHandler,
        actionHandlingContext = actionHandlingContext,
        expressionResolver = createExpressionResolver(
            variableController = variableController
        )
    )

    @Test
    fun `triggered when added and condition is true`() {
        triggerStorage.add(trigger(action = action, condition = "@{true}"))

        verifyTriggered()
    }

    @Test
    fun `not triggered when added and condition is false`() {
        triggerStorage.add(trigger(action = action, condition = "@{false}"))

        verifyNoInteractions(actionHandler)
    }

    @Test
    fun `triggered when condition changes from false to true`() {
        val variable = Variable.BooleanVariable("condition", false)
        variableController.declare(variable)

        triggerStorage.add(trigger(action = action, condition = "@{condition}"))

        verifyNoInteractions(actionHandler)

        variable.set(true)

        verifyTriggered()
    }

    @Test
    fun `not triggered when condition changes from true to false`() {
        val variable = Variable.BooleanVariable("condition", true)
        variableController.declare(variable)

        triggerStorage.add(trigger(action = action, condition = "@{condition}"))

        verifyTriggered()

        variable.set(false)

        verifyNoMoreInteractions(actionHandler)
    }

    @Test
    fun `not triggered when variable changes and mode=on_condition`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        triggerStorage.add(
            trigger(
                action = action,
                condition = "@{counter > 10}",
                mode = Mode.ON_CONDITION
            )
        )

        variable.set(20)

        verifyTriggered()

        variable.set(21)

        verifyNoMoreInteractions(actionHandler)
    }

    @Test
    fun `triggered when variable changes and mode=on_variable`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        triggerStorage.add(
            trigger(
                action = action,
                condition = "@{counter > 10}",
                mode = Mode.ON_VARIABLE
            )
        )

        variable.set(20)

        verifyTriggered()

        variable.set(21)

        verifyTriggered()
    }

    @Test
    fun `not triggered when added after stopObserving() is called`() {
        triggerStorage.stopObserving()
        triggerStorage.add(trigger(action = action, condition = "@{true}"))

        verifyNoInteractions(actionHandler)
    }

    @Test
    fun `triggered when startObserving() is called`() {
        triggerStorage.stopObserving()
        triggerStorage.add(trigger(action = action, condition = "@{true}"))

        verifyNoInteractions(actionHandler)

        triggerStorage.startObserving()

        verifyTriggered()
    }

    @Test
    fun `not triggered when startObserving() is called and was triggered before`() {
        triggerStorage.add(trigger(action = action, condition = "@{true}"))

        verifyTriggered()

        triggerStorage.stopObserving()
        triggerStorage.startObserving()

        verifyNoInteractions(actionHandler)
    }

    private fun verifyTriggered() {
        verify(actionHandler).handle(actionHandlingContext, listOf(action), DivActionSource.TRIGGER)
        clearInvocations(actionHandler)
    }
}

private val action = action(url = "test-action")
