package com.yandex.div.compose

import androidx.core.net.toUri
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import com.yandex.div2.StrVariable
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

fun variable(name: String, value: Long): DivVariable {
    return DivVariable.Integer(IntegerVariable(name = name, value = constant(value)))
}

fun variable(name: String, value: String): DivVariable {
    return DivVariable.Str(StrVariable(name = name, value = constant(value)))
}
