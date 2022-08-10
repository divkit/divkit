package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.widget.GridContainer
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import com.yandex.div2.DivGrid
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivGridBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divPatchManager: DivPatchManager,
    private val divPatchCache: DivPatchCache,
    private val divBinder: Provider<DivBinder>
) : DivViewBinder<DivGrid, DivGridLayout> {

    override fun bindView(view: DivGridLayout, div: DivGrid, divView: Div2View, path: DivStatePath) {
        val oldDiv = view.div
        if (div == oldDiv) {
            // todo MORDAANDROID-636
            // return
        }

        val resolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        view.releaseViewVisitor = divView.releaseViewVisitor
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.addSubscription(
            div.columnCount.observeAndGet(resolver) { columnCount -> view.columnCount = columnCount }
        )
        view.observeContentAlignment(div.contentAlignmentHorizontal, div.contentAlignmentVertical, resolver)

        if (oldDiv != null) {
            for (i in div.items.size..oldDiv.items.lastIndex) {
                divView.unbindViewFromDiv(view.getChildAt(i))
            }
        }
        var viewsPositionDiff = 0
        for (gridIndex in div.items.indices) {
            val childDivValue = div.items[gridIndex].value()
            val childView = view.getChildAt(gridIndex + viewsPositionDiff)
            val childDivId = childDivValue.id
            // applying div patch
            if (childDivId != null) {
                val patchViewsToAdd = divPatchManager.createViewsForId(divView, childDivId)
                val patchDivs = divPatchCache.getPatchDivListById(divView.dataTag, childDivId)
                if (patchViewsToAdd != null && patchDivs != null) {
                    view.removeViewAt(gridIndex + viewsPositionDiff)
                    for (patchIndex in patchViewsToAdd.indices) {
                        val patchDivValue = patchDivs[patchIndex].value()
                        val patchViews = patchViewsToAdd[patchIndex]
                        val layoutParams = GridContainer.LayoutParams()
                        view.addView(patchViews, gridIndex + viewsPositionDiff + patchIndex, layoutParams)
                        if (patchDivValue.hasVisibilityActions) {
                            divView.bindViewToDiv(patchViews, patchDivs[patchIndex])
                        }
                        bindLayoutParams(patchViews, childDivValue, resolver)
                    }
                    viewsPositionDiff += patchViewsToAdd.size - 1
                    continue
                }
            }
            childView.layoutParams = GridContainer.LayoutParams()
            divBinder.get().bind(childView, div.items[gridIndex], divView, path)
            bindLayoutParams(childView, childDivValue, resolver)
            if (childDivValue.hasVisibilityActions) {
                divView.bindViewToDiv(childView, div.items[gridIndex])
            } else {
                divView.unbindViewFromDiv(childView)
            }
        }

        view.trackVisibilityActions(div.items, oldDiv?.items, divView)
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

    private fun bindLayoutParams(childView: View, childDiv: DivBase, resolver: ExpressionResolver) {
        baseBinder.bindLayoutParams(childView, childDiv, resolver)

        childView.applyGridLayoutParams(resolver, childDiv)

        if (childView !is ExpressionSubscriber) return
        val callback = { _: Any -> childView.applyGridLayoutParams(resolver, childDiv) }
        childView.addSubscription(childDiv.width.weight().observe(resolver, callback))
        childView.addSubscription(childDiv.height.weight().observe(resolver, callback))
        childView.addSubscription(childDiv.columnSpan?.observe(resolver, callback) ?: Disposable.NULL)
        childView.addSubscription(childDiv.rowSpan?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private fun View.applyGridLayoutParams(resolver: ExpressionResolver, div: DivBase) {
        applyColumnWeight(resolver, div.width.weight())
        applyRowWeight(resolver, div.height.weight())
        applyColumnSpan(resolver, div.columnSpan)
        applyRowSpan(resolver, div.rowSpan)
    }

    private fun View.applyColumnWeight(resolver: ExpressionResolver, weightExpr: Expression<Double>) {
        val params = layoutParams as? GridContainer.LayoutParams ?: return
        val columnWeight = weightExpr.evaluate(resolver).toFloat()
        if (params.columnWeight != columnWeight) {
            params.columnWeight = columnWeight
            requestLayout()
        }
    }

    private fun View.applyRowWeight(resolver: ExpressionResolver, weightExpr: Expression<Double>) {
        val params = layoutParams as? GridContainer.LayoutParams ?: return
        val rowWeight = weightExpr.evaluate(resolver).toFloat()
        if (params.rowWeight != rowWeight) {
            params.rowWeight = rowWeight
            requestLayout()
        }
    }

    private fun View.applyColumnSpan(resolver: ExpressionResolver, spanExpr: Expression<Int>?) {
        val params = layoutParams as? GridContainer.LayoutParams ?: return
        val columnSpan = spanExpr?.evaluate(resolver) ?: 1
        if (params.columnSpan != columnSpan) {
            params.columnSpan = columnSpan
            requestLayout()
        }
    }

    private fun View.applyRowSpan(resolver: ExpressionResolver, spanExpr: Expression<Int>?) {
        val params = layoutParams as? GridContainer.LayoutParams ?: return
        val rowSpan = spanExpr?.evaluate(resolver) ?: 1
        if (params.rowSpan != rowSpan) {
            params.rowSpan = rowSpan
            requestLayout()
        }
    }

    private fun DivSize.weight(): Expression<Double> {
        return if (this is DivSize.MatchParent) {
            value.weight ?: DEFAULT_WEIGHT_EXPR
        } else {
            DEFAULT_WEIGHT_EXPR
        }
    }

    private companion object {
        private val DEFAULT_WEIGHT_EXPR = Expression.constant(0.0)
    }
}
