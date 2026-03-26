package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.test.data.action
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionCustom
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val customActionHandler = CustomActionHandler()
    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val actionHandler = DivActionHandler(
        customActionHandler = customActionHandler,
        reporter = reporter,
        setVariableActionHandler = mock()
    )

    private val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    private val actionHandlingContext = DivActionHandlingContext(
        expressionResolver = expressionResolver
    )

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
