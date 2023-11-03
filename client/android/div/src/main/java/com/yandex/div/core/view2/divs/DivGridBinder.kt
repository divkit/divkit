package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import com.yandex.div2.DivGrid
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

        view.releaseViewVisitor = divView.releaseViewVisitor
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.addSubscription(
            div.columnCount.observeAndGet(resolver) { columnCount -> view.columnCount = columnCount.toIntSafely() }
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
                        val layoutParams = DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                        view.addView(patchViews, gridIndex + viewsPositionDiff + patchIndex, layoutParams)
                        if (patchDivValue.hasSightActions) {
                            divView.bindViewToDiv(patchViews, patchDivs[patchIndex])
                        }
                        bindLayoutParams(patchViews, childDivValue, resolver)
                    }
                    viewsPositionDiff += patchViewsToAdd.size - 1
                    continue
                }
            }
            childView.layoutParams = DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            divBinder.get().bind(childView, div.items[gridIndex], divView, path)
            bindLayoutParams(childView, childDivValue, resolver)
            if (childDivValue.hasSightActions) {
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
        baseBinder.bindLayoutParams(childView, childDiv, null, resolver)

        childView.applyGridLayoutParams(resolver, childDiv)

        if (childView !is ExpressionSubscriber) return
        val callback = { _: Any -> childView.applyGridLayoutParams(resolver, childDiv) }
        childView.addSubscription(childDiv.columnSpan?.observe(resolver, callback) ?: Disposable.NULL)
        childView.addSubscription(childDiv.rowSpan?.observe(resolver, callback) ?: Disposable.NULL)
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

    fun setDataWithoutBinding(view: DivGridLayout, div: DivGrid, resolver: ExpressionResolver) {
        view.div = div
        for (gridIndex in div.items.indices) {
            divBinder.get().setDataWithoutBinding(
                view.getChildAt(gridIndex),
                div.items[gridIndex],
                resolver
            )
        }
    }
}
