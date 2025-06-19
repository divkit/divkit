package com.yandex.div.sizeprovider

import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSize

class DivSizeProviderVariablesHolder : DivVisitor<Unit>(), ExpressionSubscriber {

    private val changedVariables = mutableListOf<String>()

    override val subscriptions = mutableListOf<Disposable>()

    fun contains(variable: String) = changedVariables.contains(variable)

    fun clear() = changedVariables.clear()

    fun observeDivData(data: DivData, resolver: ExpressionResolver) = data.states.forEach { visit(it.div, resolver) }

    override fun defaultVisit(data: Div, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: Div.Container, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.buildItems(divView = null, resolver = resolver)
            .forEach { visit(it.div, it.expressionResolver) }
    }

    override fun visit(data: Div.Grid, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.nonNullItems.forEach { visit(it, resolver) }
    }

    override fun visit(data: Div.Gallery, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.buildItems(divView = null, resolver = resolver)
            .forEach { visit(it.div, it.expressionResolver) }
    }

    override fun visit(data: Div.Pager, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.buildItems(divView = null, resolver = resolver)
            .forEach { visit(it.div, it.expressionResolver) }
    }

    override fun visit(data: Div.Tabs, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.items.forEach { visit(it.div, resolver) }
    }

    override fun visit(data: Div.State, resolver: ExpressionResolver) {
        defaultVisit(data, resolver)
        data.value.states.forEach { state -> state.div?.let { visit(it, resolver) } }
    }

    private fun Div.observeSize(resolver: ExpressionResolver) {
        with(value()) {
            width.observe(resolver)
            height.observe(resolver)
        }
    }

    private fun DivSize.observe(resolver: ExpressionResolver) {
        val size = value() as? DivFixedSize ?: return
        val sizeExpr = size.value as? Expression.MutableExpression<*, Long> ?: return
        addSubscription(sizeExpr.observe(resolver) {
            changedVariables.addAll(sizeExpr.getVariablesName(resolver))
        })
    }
}
