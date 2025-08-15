package com.yandex.div.internal.core

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

data class DivItemBuilderResult(
    val div: Div,
    val expressionResolver: ExpressionResolver,
)
