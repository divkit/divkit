package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivTypedValue
import org.json.JSONObject

fun action(
    typed: DivActionTyped? = null,
    payload: JSONObject? = null,
    url: String? = null,
): DivAction {
    return DivAction(
        logId = constant("test"),
        payload = payload,
        typed = typed,
        url = url?.let { constant(it.toUri()) }
    )
}

fun setVariableAction(name: String, value: DivTypedValue): DivActionTyped {
    return DivActionTyped.SetVariable(
        DivActionSetVariable(value = value, variableName = constant(name))
    )
}
