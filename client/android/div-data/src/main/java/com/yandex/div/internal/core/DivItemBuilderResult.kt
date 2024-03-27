package com.yandex.div.internal.core

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

class DivItemBuilderResult(
    val div: Div,
    val expressionResolver: ExpressionResolver,
) {

    operator fun component1() = div
    operator fun component2() = expressionResolver
}
