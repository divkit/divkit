package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionCustom
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val customActionHandler = CustomActionHandler()

    private val actionHandler = actionHandlerEnvironment.createActionHandler(
        customActionHandler = customActionHandler
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
        actionHandler.handle(context = actionHandlerEnvironment.context, action = action)
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
