package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.evaluateGravity
import com.yandex.div.core.util.hasSightActions
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.reuse.util.tryRebindPlainContainerChildren
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.itemsToDivBlocks
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivGridBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val divBinder: Provider<DivBinder>,
    private val divViewCreator: Provider<DivViewCreator>,
) : DivViewBinder<DivBlock.Grid, DivGridLayout>(baseBinder) {

    override fun bindView(view: DivGridLayout, divBlock: DivBlock.Grid, divView: Div2View) {
        val oldDivBlock = view.divBlock
        if (oldDivBlock?.div === divBlock.div) {
            view.bindItems(divBlock, oldDivBlock, divView)
            return
        }
        super.bindView(view, divBlock, divView)
        view.bindItems(divBlock, oldDivBlock, divView)
    }

    override fun DivGridLayout.bind(
        divBlock: DivBlock.Grid,
        oldDivBlock: DivBlock.Grid?,
        divView: Div2View,
    ) {
        releaseViewVisitor = divView.releaseViewVisitor

        val div = divBlock.divValue
        val expressionResolver = divBlock.expressionResolver
        applyDivActions(
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.captureFocusOnAction,
            expressionResolver,
            divView,
        )

        addSubscription(
            div.columnCount.observeAndGet(expressionResolver) { columnCount = it.toIntSafely() }
        )
        observeContentAlignment(
            div.contentAlignmentHorizontal,
            div.contentAlignmentVertical,
            expressionResolver
        )
    }

    private fun DivGridLayout.bindItems(
        divBlock: DivBlock.Grid,
        oldDivBlock: DivBlock.Grid?,
        divView: Div2View,
    ) {
        val newItems = divBlock.itemsToDivBlocks()
        val oldItems = oldDivBlock?.itemsToDivBlocks() ?: emptyList()
        if (!tryRebindPlainContainerChildren(divView, newItems, divViewCreator)) {
            replaceWithReuse(divView, divViewCreator, oldItems, newItems)
        }

        dispatchBinding(newItems, divView)
        trackVisibilityActions(divView, newItems, oldItems)
    }

    private fun DivGridLayout.observeContentAlignment(
        horizontalAlignment: Expression<DivAlignmentHorizontal>,
        verticalAlignment: Expression<DivAlignmentVertical>,
        resolver: ExpressionResolver
    ) {
        gravity = evaluateGravity(horizontalAlignment.evaluate(resolver), verticalAlignment.evaluate(resolver))

        val callback = { _: Any ->
            gravity = evaluateGravity(horizontalAlignment.evaluate(resolver), verticalAlignment.evaluate(resolver))
        }
        addSubscription(horizontalAlignment.observe(resolver, callback))
        addSubscription(verticalAlignment.observe(resolver, callback))
    }

    private fun DivGridLayout.dispatchBinding(items: List<DivBlock>, divView: Div2View) {
        items.forEachIndexed { index, item ->
            val childView = getChildAt(index)
            val childDiv = item.div

            divBinder.get().bind(childView, item, divView)
            bindLayoutParams(childView, childDiv.value(), item.expressionResolver)
            if (childDiv.value().hasSightActions) {
                divView.bindViewToDiv(childView, childDiv)
            } else {
                divView.unbindViewFromDiv(childView)
            }
        }
    }

    private fun bindLayoutParams(childView: View, childDiv: DivBase, resolver: ExpressionResolver) {
        childView.applyGridLayoutParams(resolver, childDiv)

        if (childView !is ExpressionSubscriber) return
        val callback = { _: Any -> childView.applyGridLayoutParams(resolver, childDiv) }
        childView.addSubscription(childDiv.columnSpan?.observe(resolver, callback))
        childView.addSubscription(childDiv.rowSpan?.observe(resolver, callback))
    }

    private fun View.applyGridLayoutParams(resolver: ExpressionResolver, div: DivBase) {
        applyColumnSpan(resolver, div.columnSpan)
        applyRowSpan(resolver, div.rowSpan)
    }

    private fun View.applyColumnSpan(resolver: ExpressionResolver, spanExpr: Expression<Long>?) {
        val params = layoutParams as? DivLayoutParams ?: return
        val columnSpan = spanExpr?.evaluate(resolver)?.toIntSafely() ?: 1
        if (params.columnSpan != columnSpan) {
            params.columnSpan = columnSpan
            requestLayout()
        }
    }

    private fun View.applyRowSpan(resolver: ExpressionResolver, spanExpr: Expression<Long>?) {
        val params = layoutParams as? DivLayoutParams ?: return
        val rowSpan = spanExpr?.evaluate(resolver)?.toIntSafely() ?: 1
        if (params.rowSpan != rowSpan) {
            params.rowSpan = rowSpan
            requestLayout()
        }
    }
}
