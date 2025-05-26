package com.yandex.div.core.view2

import com.yandex.div.core.expression.local.asImpl
import com.yandex.div.json.expressions.ExpressionResolver

internal class BindingContext private constructor(
    val divView: Div2View,
    val expressionResolver: ExpressionResolver,
) {

    val runtimeStore = expressionResolver.asImpl?.runtimeStore

    fun getFor(resolver: ExpressionResolver) =
        if (expressionResolver == resolver) this else BindingContext(divView, resolver)

    companion object {
        fun createEmpty(divView: Div2View) = BindingContext(divView, ExpressionResolver.EMPTY)
    }
}
