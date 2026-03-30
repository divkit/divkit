package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.ArrayValue
import com.yandex.div2.ColorValue
import com.yandex.div2.DivTypedValue
import com.yandex.div2.IntegerValue
import com.yandex.div2.StrValue
import org.json.JSONArray

fun typedValue(value: String): DivTypedValue {
    return DivTypedValue.Str(StrValue(value = constant(value)))
}

fun typedValue(value: Long): DivTypedValue {
    return DivTypedValue.Integer(IntegerValue(value = constant(value)))
}

fun typedValue(value: Expression<Long>): DivTypedValue {
    return DivTypedValue.Integer(IntegerValue(value = value))
}

fun typedValue(value: JSONArray): DivTypedValue {
    return DivTypedValue.Array(ArrayValue(value = constant(value)))
}

fun typedColorValue(value: Long): DivTypedValue {
    return DivTypedValue.Color(ColorValue(value = constant(value.toInt())))
}
