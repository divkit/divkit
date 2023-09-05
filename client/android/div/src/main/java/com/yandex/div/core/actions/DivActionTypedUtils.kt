package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTypedValue

internal fun Div2View.logError(throwable: Throwable) {
    viewComponent
        .errorCollectors
        .getOrCreate(dataTag, divData)
        .logError(throwable)
}

internal fun DivTypedValue.evaluate(expressionResolver: ExpressionResolver): Any {
    val newValue: Any = when (this) {
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Str -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Bool -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Color -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Number -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Url -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Array -> value.value
        is DivTypedValue.Dict -> value.value
    }
    return newValue
}
