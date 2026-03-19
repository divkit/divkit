package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.action
import com.yandex.div.compose.constant
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionCustom
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivTypedValue
import com.yandex.div2.IntegerValue
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val customActionHandler = CustomActionHandler()
    private val reporter = mock<DivReporter>()

    private val actionHandler = DivActionHandler(
        customActionHandler = customActionHandler,
        reporter = reporter,
        setVariableActionHandler = SetVariableActionHandler(
            reporter = reporter
        )
    )

    private val variableController = DivVariableController()

    private val expressionResolver = DivComposeExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    private val actionHandlingContext = DivActionHandlingContext(
        expressionResolver = expressionResolver
    )

    @Test
    fun `handle set_variable action`() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        handle(
            action(
                typed = DivActionTyped.SetVariable(
                    DivActionSetVariable(
                        value = DivTypedValue.Integer(IntegerValue(value = constant(20))),
                        variableName = constant("counter")
                    )
                )
            )
        )

        assertEquals(20L, variableController.get("counter")?.getValue())
    }

    @Test
    fun `handle set_variable url action`() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        handle(action(url = "div-action://set_variable?name=counter&value=20"))

        assertEquals(20L, variableController.get("counter")?.getValue())
    }

    @Test
    fun `handle custom action`() {
        val payload = JSONObject().apply {
            put("key", "value")
        }

        handle(
            action(
                payload = payload,
                typed = DivActionTyped.Custom(DivActionCustom())
            )
        )

        assertEquals(payload, customActionHandler.lastAction?.payload)
    }

    private fun handle(action: DivAction) {
        actionHandler.handle(context = actionHandlingContext, action = action)
    }
}

private class CustomActionHandler : DivCustomActionHandler {
    var lastAction: DivActionData? = null
        private set

    override fun handle(
        context: DivActionHandlingContext,
        action: DivActionData
    ) {
        lastAction = action
    }
}
