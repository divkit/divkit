package com.yandex.div.core.view2.divs

import com.yandex.div.core.Disposable
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSize

internal class DivLayoutProviderVariableHolder : DivTreeVisitor<Unit>(), ExpressionSubscriber {

    private val changedVariables = mutableListOf<String>()

    override val subscriptions = mutableListOf<Disposable>()

    fun contains(variable: String) = changedVariables.contains(variable)

    fun clear() = changedVariables.clear()

    fun observeDivDataIfNeeded(data: DivData, context: BindingContext) {
        if (subscriptions.isNotEmpty()) return
        data.states.forEach { visit(it.div, context, DivStatePath.fromState(it)) }
    }

    override fun defaultVisit(data: Div, context: BindingContext, path: DivStatePath) =
        data.value().observeSize(context.expressionResolver)

    private fun DivBase.observeSize(resolver: ExpressionResolver) {
        width.observe(resolver)
        height.observe(resolver)
    }

    private fun DivSize.observe(resolver: ExpressionResolver) {
        val size = value() as? DivFixedSize ?: return
        val sizeExpr = size.value as? Expression.MutableExpression<*, Long> ?: return
        addSubscription(sizeExpr.observe(resolver) {
            changedVariables.addAll(sizeExpr.getVariablesName(resolver))
        })
    }
}
