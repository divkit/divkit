package com.yandex.div.compose

import androidx.core.net.toUri
import com.yandex.div.evaluable.types.Color
import com.yandex.div2.ArrayValue
import com.yandex.div2.ColorValue
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivTypedValue
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivTrigger.Mode
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerValue
import com.yandex.div2.IntegerVariable
import com.yandex.div2.StrVariable
import com.yandex.div2.StrValue
import org.json.JSONArray
import org.json.JSONObject

fun variable(name: String, value: Long): DivVariable {
    return DivVariable.Integer(IntegerVariable(name = name, value = constant(value)))
}

fun variable(name: String, value: String): DivVariable {
    return DivVariable.Str(StrVariable(name = name, value = constant(value)))
}

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

fun typedValue(value: String): DivTypedValue {
    return DivTypedValue.Str(StrValue(value = constant(value)))
}

fun typedValue(value: Long): DivTypedValue {
    return DivTypedValue.Integer(IntegerValue(value = constant(value)))
}

fun typedValue(value: JSONArray): DivTypedValue {
    return DivTypedValue.Array(ArrayValue(value = constant(value)))
}

fun typedColorValue(value: Long): DivTypedValue {
    return DivTypedValue.Color(ColorValue(value = constant(value.toInt())))
}

fun color(value: Long) = Color(value.toInt())

fun trigger(
    action: DivAction,
    condition: String,
    mode: Mode = Mode.ON_CONDITION
): DivTrigger {
    return DivTrigger(
        actions = listOf(action),
        condition = booleanExpression(condition),
        mode = constant(mode)
    )
}
