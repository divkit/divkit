package com.yandex.div.internal.variables

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject

@InternalApi
@Throws(VariableMutationException::class)
fun Variable.castAndSetValue(value: Any) {
    when (this) {
        is Variable.ArrayVariable ->
            set(checkValueAndCast<JSONArray>(variableName = name, value = value))

        is Variable.BooleanVariable ->
            set(checkValueAndCast<Boolean>(variableName = name, value = value))

        is Variable.ColorVariable ->
            set(checkValueAndCast<Int>(variableName = name, value = value).let(::Color))

        is Variable.DictVariable ->
            set(checkValueAndCast<JSONObject>(variableName = name, value = value))

        is Variable.DoubleVariable ->
            set(checkValueAndCast<Double>(variableName = name, value = value))

        is Variable.IntegerVariable ->
            set(checkValueAndCast<Long>(variableName = name, value = value))

        is Variable.StringVariable ->
            set(checkValueAndCast<String>(variableName = name, value = value))

        is Variable.UrlVariable ->
            set(checkValueAndCast<Uri>(variableName = name, value = value))

        is Variable.PropertyVariable ->
            set(checkValueAndCast(value))
    }
}

private fun Variable.PropertyVariable.checkValueAndCast(value: Any): Any {
    return when (valueType) {
        DivEvaluableType.ARRAY -> checkValueAndCast<JSONArray>(variableName = name, value = value)
        DivEvaluableType.BOOLEAN -> checkValueAndCast<Boolean>(variableName = name, value = value)
        DivEvaluableType.COLOR -> checkValueAndCast<Color>(variableName = name, value = value)
        DivEvaluableType.DATETIME -> checkValueAndCast<DateTime>(variableName = name, value = value)
        DivEvaluableType.DICT -> checkValueAndCast<JSONObject>(variableName = name, value = value)
        DivEvaluableType.INTEGER -> checkValueAndCast<Long>(variableName = name, value = value)
        DivEvaluableType.NUMBER -> checkValueAndCast<Double>(variableName = name, value = value)
        DivEvaluableType.STRING -> checkValueAndCast<String>(variableName = name, value = value)
        DivEvaluableType.URL -> checkValueAndCast<Url>(variableName = name, value = value)
    }
}

private inline fun <reified T : Any> checkValueAndCast(
    variableName: String,
    value: Any
): T {
    (value as? T)?.let {
        return it
    }

    val valueType = when (value) {
        is Double -> "number"
        is JSONArray -> "array"
        is JSONObject -> "dict"
        else -> value.javaClass.simpleName.lowercase()
    }
    throw VariableMutationException(
        "Trying to set value with invalid type ($valueType) to variable $variableName"
    )
}
