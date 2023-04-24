package com.yandex.div.sizeprovider

import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSize
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo

class DivSizeProviderVariablesHolder : DivVisitor<Unit>(), ExpressionSubscriber {

    private val changedVariables = mutableListOf<String>()

    override val subscriptions = mutableListOf<Disposable>()

    fun contains(variable: String) = changedVariables.contains(variable)

    fun clear() = changedVariables.clear()

    fun observeDivData(data: DivData, resolver: ExpressionResolver) = data.states.forEach { visit(it.div, resolver) }

    override fun visit(data: DivText, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivImage, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivGifImage, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivSeparator, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivContainer, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.items.forEach { visit(it, resolver) }
    }

    override fun visit(data: DivGrid, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.items.forEach { visit(it, resolver) }
    }

    override fun visit(data: DivGallery, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.items.forEach { visit(it, resolver) }
    }

    override fun visit(data: DivPager, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.items.forEach { visit(it, resolver) }
    }

    override fun visit(data: DivTabs, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.items.forEach { visit(it.div, resolver) }
    }

    override fun visit(data: DivState, resolver: ExpressionResolver) {
        data.observeSize(resolver)
        data.states.forEach { state ->
            state.div?.let { visit(it, resolver) }
        }
    }

    override fun visit(data: DivCustom, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivIndicator, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivSlider, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivInput, resolver: ExpressionResolver) = data.observeSize(resolver)

    override fun visit(data: DivVideo, resolver: ExpressionResolver) = data.observeSize(resolver)

    private fun DivBase.observeSize(resolver: ExpressionResolver) {
        width.observe(resolver)
        height.observe(resolver)
    }

    private fun DivSize.observe(resolver: ExpressionResolver) {
        val size = value() as? DivFixedSize ?: return
        val sizeExpr = size.value as? Expression.MutableExpression<*, Int> ?: return
        addSubscription(sizeExpr.observe(resolver) {
            changedVariables.addAll(sizeExpr.getVariablesName())
        })
    }
}
