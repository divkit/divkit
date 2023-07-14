@file:JvmName("DivVariables")
@file:Suppress("unused")

package com.yandex.div

import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div2.BoolVariable
import com.yandex.div2.ColorVariable
import com.yandex.div2.DictVariable
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import com.yandex.div2.NumberVariable
import com.yandex.div2.StrVariable
import com.yandex.div2.UrlVariable
import org.json.JSONObject

fun integerVariable(name: String, value: Long): DivVariable.Integer {
    return DivVariable.Integer(IntegerVariable(name, value))
}

fun numberVariable(name: String, value: Double): DivVariable.Number {
    return DivVariable.Number(NumberVariable(name, value))
}

fun boolVariable(name: String, value: Boolean): DivVariable.Bool {
    return DivVariable.Bool(BoolVariable(name, value))
}

fun stringVariable(name: String, value: String): DivVariable.Str {
    return DivVariable.Str(StrVariable(name, value))
}

fun colorVariable(name: String, @ColorInt value: Int): DivVariable.Color {
    return DivVariable.Color(ColorVariable(name, value))
}

fun urlVariable(name: String, value: Uri): DivVariable.Url {
    return DivVariable.Url(UrlVariable(name, value))
}

internal fun dictVariable(name: String, value: JSONObject): DivVariable.Dict {
    return DivVariable.Dict(DictVariable(name, value))
}
