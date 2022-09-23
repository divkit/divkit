package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivContainerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divViewCreator: Provider<DivViewCreator>,
    private val divPatchManager: DivPatchManager,
    private val divPatchCache: DivPatchCache,
    private val divBinder: Provider<DivBinder>
) : DivViewBinder<DivContainer, ViewGroup> {

    override fun bindView(view: ViewGroup, div: DivContainer, divView: Div2View, path: DivStatePath) {
        var oldDiv = when (view) {
            is DivWrapLayout -> view.div
            is DivLinearLayout -> view.div
            is DivFrameLayout -> view.div
            else -> null
        }

        if (div == oldDiv) {
            // todo MORDAANDROID-636
            // return
        }

        val resolver = divView.expressionResolver

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        val expressionSubscriber = view.expressionSubscriber
        expressionSubscriber.closeAllSubscription()
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        val areDivsReplaceable = DivComparator.areDivsReplaceable(oldDiv, div, resolver)
        when (view) {
            is DivLinearLayout -> bindLinearProperties(view, div, resolver, expressionSubscriber)
            is DivWrapLayout -> bindWrapProperties(view, div, resolver, expressionSubscriber)
            is DivFrameLayout -> view.div = div
        }

        for (childView in view.children) {
            divView.unbindViewFromDiv(childView)
        }
        if (!areDivsReplaceable && oldDiv != null) {
            oldDiv = null
            view.releaseAndRemoveChildren(divView)
            div.items.forEach { childData: Div ->
                view.addView(divViewCreator.get().create(childData, divView.expressionResolver))
            }
        }
        for (i in div.items.indices) {
            if (div.items[i].value().hasVisibilityActions) {
                divView.bindViewToDiv(view.getChildAt(i), div.items[i])
            }
        }

        var viewsPositionDiff = 0
        for (containerIndex in div.items.indices) {
            val childDivValue = div.items[containerIndex].value()
            val childView = view.getChildAt(containerIndex + viewsPositionDiff)
            val childDivId = childDivValue.id
            // applying div patch
            if (childDivId != null) {
                val patchViewsToAdd = divPatchManager.createViewsForId(divView, childDivId)
                val patchDivs = divPatchCache.getPatchDivListById(divView.dataTag, childDivId)
                if (patchViewsToAdd != null && patchDivs != null) {
                    view.removeViewAt(containerIndex + viewsPositionDiff)
                    for (patchIndex in patchViewsToAdd.indices) {
                        val patchDivValue = patchDivs[patchIndex].value()
                        val patchView = patchViewsToAdd[patchIndex]
                        view.addView(patchView, containerIndex + viewsPositionDiff + patchIndex)
                        observeChildViewAlignment(div, patchDivValue, patchView, resolver, expressionSubscriber)
                        if (patchDivValue.hasVisibilityActions) {
                            divView.bindViewToDiv(patchView, patchDivs[patchIndex])
                        }
                    }
                    viewsPositionDiff += patchViewsToAdd.size - 1
                    continue
                }
            }

            divBinder.get().bind(childView, div.items[containerIndex], divView, path)
            observeChildViewAlignment(div, childDivValue, childView, resolver, expressionSubscriber)
        }

        view.trackVisibilityActions(div.items, oldDiv?.items, divView)
    }

    private fun bindLinearProperties(
        view: DivLinearLayout,
        div: DivContainer,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        subscriber.addSubscription(div.orientation.observeAndGet(resolver) {
            view.orientation = if (div.isHorizontal(resolver))
                LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        })
        subscriber.addSubscription(div.contentAlignmentHorizontal.observeAndGet(resolver) {
            view.gravity = evaluateGravity(it, div.contentAlignmentVertical.evaluate(resolver))
        })
        subscriber.addSubscription(div.contentAlignmentVertical.observeAndGet(resolver) {
            view.gravity = evaluateGravity(div.contentAlignmentHorizontal.evaluate(resolver), it)
        })

        view.div = div
    }

    private fun bindWrapProperties(
            view: DivWrapLayout,
            div: DivContainer,
            resolver: ExpressionResolver,
            subscriber: ExpressionSubscriber
    ) {
        subscriber.addSubscription(div.orientation.observeAndGet(resolver) {
            view.wrapDirection =
                    if (div.isHorizontal(resolver)) WrapDirection.ROW else WrapDirection.COLUMN
        })

        view.div = div
    }

    private fun observeChildViewAlignment(
        div: DivContainer,
        childDivValue: DivBase,
        childView: View,
        resolver: ExpressionResolver,
        expressionSubscriber: ExpressionSubscriber,
    ) {
        val applyAlignments = { _: Any ->
            val alignmentHorizontal = childDivValue.alignmentHorizontal ?: div.contentAlignmentHorizontal
            val alignmentVertical = childDivValue.alignmentVertical ?: div.contentAlignmentVertical

            childView.applyAlignment(alignmentHorizontal.evaluate(resolver),
                alignmentVertical.evaluate(resolver))

            if (div.isVertical(resolver) && childDivValue.height is DivSize.MatchParent) {
                childView.applyWeight(childDivValue.height.value() as DivMatchParentSize, resolver)
                if (!div.isWrapContainer(resolver)) {
                    ForceParentLayoutParams.setSizeFromParent(childView, h = 0)
                }
            } else if (div.isHorizontal(resolver) && childDivValue.width is DivSize.MatchParent) {
                childView.applyWeight(childDivValue.width.value() as DivMatchParentSize, resolver)
                if (!div.isWrapContainer(resolver)) {
                    ForceParentLayoutParams.setSizeFromParent(childView, w = 0)
                }
            }
        }

        expressionSubscriber.addSubscription(
            div.contentAlignmentHorizontal.observe(resolver, applyAlignments)
        )
        expressionSubscriber.addSubscription(
            div.contentAlignmentVertical.observe(resolver, applyAlignments)
        )
        expressionSubscriber.addSubscription(
            div.orientation.observe(resolver, applyAlignments)
        )

        if ((div.isVertical(resolver) && childDivValue.height is DivSize.MatchParent)) {
            (childDivValue.height.value() as DivMatchParentSize).weight?.let {
                expressionSubscriber.addSubscription(
                    it.observe(resolver, applyAlignments)
                )
            }
        } else if(div.isHorizontal(resolver) && childDivValue.width is DivSize.MatchParent) {
            (childDivValue.width.value() as DivMatchParentSize).weight?.let {
                expressionSubscriber.addSubscription(
                    it.observe(resolver, applyAlignments)
                )
            }
        }

        applyAlignments(childView)
    }

    private fun View.applyWeight(size: DivMatchParentSize, resolver: ExpressionResolver) {
        val params = layoutParams
        if (params is LinearLayout.LayoutParams) {
            params.weight = size.weight?.evaluate(resolver)?.toFloat() ?: 1.0f
        }
    }

    private fun DivContainer.isHorizontal(resolver: ExpressionResolver) =
            orientation.evaluate(resolver) == DivContainer.Orientation.HORIZONTAL

    private fun DivContainer.isVertical(resolver: ExpressionResolver) =
            orientation.evaluate(resolver) == DivContainer.Orientation.VERTICAL

    private fun DivContainer.isWrapContainer(resolver: ExpressionResolver) =
            layoutMode.evaluate(resolver) == DivContainer.LayoutMode.WRAP
}
