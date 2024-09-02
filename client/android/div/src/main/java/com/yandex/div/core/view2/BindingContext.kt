package com.yandex.div.core.view2

import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.json.expressions.ExpressionResolver

internal class BindingContext private constructor(
    val divView: Div2View,
    val expressionResolver: ExpressionResolver,
    val runtimeStore: RuntimeStore?,
) {
    fun getFor(resolver: ExpressionResolver) =
        if (expressionResolver == resolver) this else BindingContext(divView, resolver, runtimeStore)

    fun getFor(resolver: ExpressionResolver, runtimeStore: RuntimeStore?) =
        if (expressionResolver == resolver) this else BindingContext(divView, resolver, runtimeStore)

    companion object {
        fun createEmpty(divView: Div2View) = BindingContext(divView, ExpressionResolver.EMPTY, null)
    }
}
