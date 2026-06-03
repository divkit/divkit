package com.yandex.div.compose

import androidx.core.net.toUri
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivCustomActionData
import com.yandex.div.compose.actions.DivExternalActionHandler
import org.json.JSONObject
import kotlin.test.assertEquals

class TestExternalActionHandler : DivExternalActionHandler {
    val handledActions = mutableListOf<DivActionData>()
    val handledCustomActions = mutableListOf<DivCustomActionData>()

    val handledAction: DivActionData
        get() {
            val size = handledActions.size
            assertEquals(
                expected = 1,
                actual = size,
                message = "Expected one handled action. Actual count: $size"
            )
            return handledActions.single()
        }

    val handledCustomAction: DivCustomActionData
        get() {
            val size = handledCustomActions.size
            assertEquals(
                expected = 1,
                actual = size,
                message = "Expected one handled action. Actual count: $size"
            )
            return handledCustomActions.single()
        }

    override fun handle(context: DivActionHandlingContext, action: DivActionData) {
        handledActions.add(action)
    }

    override fun handleCustomAction(
        context: DivActionHandlingContext,
        action: DivCustomActionData
    ) {
        handledCustomActions.add(action)
    }

    fun reset() {
        handledActions.clear()
        handledCustomActions.clear()
    }
}

fun actionData(
    id: String = "test",
    payload: JSONObject? = null,
    source: DivActionSource = DivActionSource.TAP,
    url: String? = null
): DivActionData {
    return DivActionData(
        id = id,
        payload = payload,
        source = source,
        url = url?.toUri()
    )
}
