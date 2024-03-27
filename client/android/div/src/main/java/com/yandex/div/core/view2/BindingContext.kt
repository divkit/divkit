package com.yandex.div.core.view2

import com.yandex.div.json.expressions.ExpressionResolver

internal data class BindingContext(
    val divView: Div2View,
    val expressionResolver: ExpressionResolver
)
