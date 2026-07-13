package com.yandex.divkit.demo.utils

import androidx.core.net.toUri
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivAction
import org.json.JSONException
import org.json.JSONObject

/**
 * Dispatches an action, given as a JSON object or a URI string, to this [Div2View] via its own
 * action machinery, so built-in actions (e.g. `set_variable`, timers) are handled by the core
 * action performer and target the view that actually holds the data.
 */
fun Div2View.handleActionString(action: String) {
    try {
        handleAction(parseDivAction(action))
    } catch (_: JSONException) {
        handleUri(action.toUri())
    }
}

private fun parseDivAction(action: String): DivAction {
    val env = DivParsingEnvironment(ParsingErrorLogger.LOG)
    return DivAction(env, JSONObject(action))
}
