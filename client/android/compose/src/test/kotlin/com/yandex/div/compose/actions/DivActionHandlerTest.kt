package com.yandex.div.compose.actions

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.customAction
import com.yandex.div2.DivAction
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val externalActionHandler = TestExternalActionHandler()

    private val actionHandler = actionHandlerEnvironment.createActionHandler(
        externalActionHandler = externalActionHandler
    )

    @Test
    fun `not enabled action is not handled`() {
        handle(action(isEnabled = false, typed = customAction()))

        assertNull(externalActionHandler.lastCustomAction)
    }

    @Test
    fun `unhandled action is passed to the external action handler`() {
        val payload = JSONObject(mapOf("key" to "value"))

        handle(
            action(
                id = "test",
                payload = payload,
                url = "custom://url"
            )
        )

        assertEquals(
            DivActionData(
                id = "test",
                payload = payload,
                url = "custom://url".toUri()
            ),
            externalActionHandler.lastAction
        )
    }

    @Test
    fun `custom action is passed to the external action handler`() {
        val payload = JSONObject(mapOf("key" to "value"))

        handle(
            action(
                id = "test",
                payload = payload,
                typed = customAction()
            )
        )

        assertEquals(
            DivCustomActionData(
                id = "test",
                payload = payload
            ),
            externalActionHandler.lastCustomAction
        )
    }

    private fun handle(action: DivAction) {
        actionHandler.handle(context = actionHandlerEnvironment.context, action = action)
    }
}

private class TestExternalActionHandler : DivExternalActionHandler {
    var lastAction: DivActionData? = null
        private set

    var lastCustomAction: DivCustomActionData? = null
        private set

    override fun handle(context: DivActionHandlingContext, action: DivActionData) {
        lastAction = action
    }

    override fun handleCustomAction(
        context: DivActionHandlingContext,
        action: DivCustomActionData
    ) {
        lastCustomAction = action
    }
}
