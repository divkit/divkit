package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.json.expressions.ExpressionResolver

@PublicApi
class DivActionHandlingContext(
    val expressionResolver: ExpressionResolver
)
