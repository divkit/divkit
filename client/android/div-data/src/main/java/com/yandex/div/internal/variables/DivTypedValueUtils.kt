package com.yandex.div.internal.variables

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTypedValue

@InternalApi
fun DivTypedValue.evaluate(expressionResolver: ExpressionResolver): Any {
    return when (this) {
        is DivTypedValue.Array -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Bool -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Color -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Dict -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Number -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Str -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Url -> value.value.evaluate(expressionResolver)
    }
}

@InternalApi
fun DivTypedValue.evaluateAsPrimitive(expressionResolver: ExpressionResolver): Any {
    return when (this) {
        is DivTypedValue.Array -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Bool -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Color -> com.yandex.div.evaluable.types.Color(value.value.evaluate(expressionResolver)).toString()
        is DivTypedValue.Dict -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Number -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Str -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Url -> value.value.evaluate(expressionResolver).toString()
    }
}
