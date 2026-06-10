package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.StoredValue
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTypedValue

@InternalApi
fun DivTypedValue.toStoredValue(
    name: String,
    expressionResolver: ExpressionResolver
): StoredValue {
    return when (this) {
        is DivTypedValue.Str ->
            StoredValue.StringStoredValue(name, value.value.evaluate(expressionResolver))

        is DivTypedValue.Integer ->
            StoredValue.IntegerStoredValue(name, value.value.evaluate(expressionResolver))

        is DivTypedValue.Bool ->
            StoredValue.BooleanStoredValue(name, value.value.evaluate(expressionResolver))

        is DivTypedValue.Number ->
            StoredValue.DoubleStoredValue(name, value.value.evaluate(expressionResolver))

        is DivTypedValue.Color ->
            StoredValue.ColorStoredValue(name, Color(value.value.evaluate(expressionResolver)))

        is DivTypedValue.Url ->
            StoredValue.UrlStoredValue(
                name,
                Url.from(value.value.evaluate(expressionResolver).toString())
            )

        is DivTypedValue.Array ->
            StoredValue.ArrayStoredValue(name, value.value.evaluate(expressionResolver))

        is DivTypedValue.Dict ->
            StoredValue.DictStoredValue(name, value.value.evaluate(expressionResolver))
    }
}
