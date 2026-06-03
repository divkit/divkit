package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.actionData
import com.yandex.div.test.data.action
import com.yandex.div.test.data.customAction
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivAction
import org.json.JSONObject
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val externalActionHandler = TestExternalActionHandler()

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            externalActionHandler = externalActionHandler
        )
    }

    @Test
    fun `not enabled action is not handled`() {
        handle(
            action(
                isEnabled = false,
                url = "custom://url"
            )
        )

        assertEquals(emptyList(), externalActionHandler.handledActions)
    }

    @Test
    fun `empty action is passed to the external action handler`() {
        handle(
            action(id = "test"),
            source = DivActionSource.TAP
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.TAP
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `unhandled action is passed to the external action handler`() {
        val payload = JSONObject(mapOf("key" to "value"))

        handle(
            action(
                id = "test",
                payload = payload,
                url = "custom://url"
            ),
            source = DivActionSource.TAP
        )

        assertEquals(
            actionData(
                id = "test",
                payload = payload,
                source = DivActionSource.TAP,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
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
            ),
            source = DivActionSource.TRIGGER
        )

        assertEquals(
            DivCustomActionData(
                id = "test",
                payload = payload,
                source = DivActionSource.TRIGGER
            ),
            externalActionHandler.handledCustomAction
        )
    }

    @Test
    fun `unhandled visibility action is passed to the external action handler`() {
        actionHandlerEnvironment.handle(
            visibilityAction(
                id = "test",
                url = "custom://url"
            )
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.VISIBILITY,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `unhandled disappear action is passed to the external action handler`() {
        actionHandlerEnvironment.handle(
            disappearAction(
                id = "test",
                url = "custom://url"
            )
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.DISAPPEAR,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
        )
    }

    private fun handle(action: DivAction, source: DivActionSource = DivActionSource.EXTERNAL) {
        actionHandlerEnvironment.handle(action, source)
    }
}
