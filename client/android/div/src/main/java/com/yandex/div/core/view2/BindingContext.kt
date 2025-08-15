package com.yandex.div.core.view2

import com.yandex.div.json.expressions.ExpressionResolver

internal class BindingContext(
    val divView: Div2View,
    val expressionResolver: ExpressionResolver,
) {

    fun getFor(resolver: ExpressionResolver) =
        if (expressionResolver == resolver) this else BindingContext(divView, resolver)
}
