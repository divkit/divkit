package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.reuse.util.tryRebindPlainContainerChildren
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toDivItemBuilderResult
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
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
    private val divBinder: Provider<DivBinder>,
    private val divViewCreator: Provider<DivViewCreator>,
) : DivViewBinder<Div.Grid, DivGrid, DivGridLayout>(baseBinder) {

    override fun bindView(context: BindingContext, view: DivGridLayout, div: Div.Grid, path: DivStatePath) {
        super.bindView(context, view, div, path)
        view.bindItems(context, div.value, view.div?.value, path)
    }

    override fun DivGridLayout.bind(bindingContext: BindingContext, div: DivGrid, oldDiv: DivGrid?) {
        releaseViewVisitor = bindingContext.divView.releaseViewVisitor

        applyDivActions(
            bindingContext,
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
        )

        addSubscription(
            div.columnCount.observeAndGet(bindingContext.expressionResolver) { columnCount = it.toIntSafely() }
        )
        observeContentAlignment(
            div.contentAlignmentHorizontal,
            div.contentAlignmentVertical,
            bindingContext.expressionResolver
        )
    }

    private fun DivGridLayout.bindItems(
        bindingContext: BindingContext,
        div: DivGrid,
        oldDiv: DivGrid?,
        path: DivStatePath
    ) {
        val resolver = bindingContext.expressionResolver
        val items = div.nonNullItems

        tryRebindPlainContainerChildren(bindingContext.divView, items.toDivItemBuilderResult(resolver), divViewCreator)

        val dispatchedItems = dispatchBinding(bindingContext, items, path)
        trackVisibilityActions(
            bindingContext.divView,
            dispatchedItems.toDivItemBuilderResult(resolver),
            oldDiv?.items?.toDivItemBuilderResult(resolver),
        )
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

    private fun DivGridLayout.dispatchBinding(
        bindingContext: BindingContext,
        items: List<Div>,
        path: DivStatePath
    ): List<Div> {
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver
        var shift = 0

        val patchedItems = items.flatMapIndexed { index, item ->
            applyPatchToChild(
                bindingContext,
                item,
                index + shift
            ).also { shift += it.size - 1 }
        }

        patchedItems.forEachIndexed { index, item ->
            val childView = getChildAt(index)
            val childDiv = item.value()
            val childPath = childDiv.resolvePath(index, path)

            childView.layoutParams = DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            divBinder.get().bind(bindingContext, childView, item, childPath)
            bindLayoutParams(childView, childDiv, resolver)
            if (childDiv.hasSightActions) {
                divView.bindViewToDiv(childView, item)
            } else {
                divView.unbindViewFromDiv(childView)
            }
        }
        return patchedItems
    }

    private fun ViewGroup.applyPatchToChild(
        bindingContext: BindingContext,
        childDiv: Div,
        childIndex: Int
    ): List<Div> {
        val divView = bindingContext.divView
        val childId = childDiv.value().id
        if (childId != null && !divView.complexRebindInProgress) {
            val patch = divPatchManager.createViewsForId(bindingContext, childId) ?: return listOf(childDiv)
            removeViewAt(childIndex)
            var shift = 0
            patch.forEach { (_, patchView) ->
                addView(patchView, childIndex + shift++, DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            }
            return patch.keys.toList()
        }
        return listOf(childDiv)
    }

    private fun bindLayoutParams(childView: View, childDiv: DivBase, resolver: ExpressionResolver) {
        baseBinder.bindLayoutParams(childView, childDiv, null, resolver, childView.expressionSubscriber)

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

    fun setDataWithoutBinding(bindingContext: BindingContext, view: DivGridLayout, div: Div.Grid) {
        view.div = div
        val items = div.value.nonNullItems
        for (gridIndex in items.indices) {
            val childView = view.getChildAt(gridIndex)
            val context = childView.bindingContext ?: bindingContext
            divBinder.get().setDataWithoutBinding(context, childView, items[gridIndex])
        }
    }
}
