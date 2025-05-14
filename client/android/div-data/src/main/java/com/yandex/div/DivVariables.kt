@file:JvmName("DivVariables")
@file:Suppress("unused")

package com.yandex.div

import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.ArrayVariable
import com.yandex.div2.BoolVariable
import com.yandex.div2.ColorVariable
import com.yandex.div2.DictVariable
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import com.yandex.div2.NumberVariable
import com.yandex.div2.StrVariable
import com.yandex.div2.UrlVariable
import org.json.JSONArray
import org.json.JSONObject

fun integerVariable(name: String, value: Long): DivVariable.Integer {
    return DivVariable.Integer(IntegerVariable(name, Expression.constant(value)))
}

fun numberVariable(name: String, value: Double): DivVariable.Number {
    return DivVariable.Number(NumberVariable(name, Expression.constant(value)))
}

fun boolVariable(name: String, value: Boolean): DivVariable.Bool {
    return DivVariable.Bool(BoolVariable(name, Expression.constant(value)))
}

fun stringVariable(name: String, value: String): DivVariable.Str {
    return DivVariable.Str(StrVariable(name, Expression.constant(value)))
}

fun colorVariable(name: String, @ColorInt value: Int): DivVariable.Color {
    return DivVariable.Color(ColorVariable(name, Expression.constant(value)))
}

fun urlVariable(name: String, value: Uri): DivVariable.Url {
    return DivVariable.Url(UrlVariable(name, Expression.constant(value)))
}

internal fun dictVariable(name: String, value: JSONObject): DivVariable.Dict {
    return DivVariable.Dict(DictVariable(name, Expression.constant(value)))
}

internal fun arrayVariable(name: String, value: JSONArray): DivVariable.Array {
    return DivVariable.Array(ArrayVariable(name, Expression.constant(value)))
}
