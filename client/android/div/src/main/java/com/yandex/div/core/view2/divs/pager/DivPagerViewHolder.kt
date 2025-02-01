package com.yandex.div.core.view2.divs.pager

import android.view.Gravity
import com.yandex.div.R
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.doOnEveryDetach
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionViewHolder
import com.yandex.div.internal.KLog
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import com.yandex.div2.DivPager.ItemAlignment
import javax.inject.Provider

internal class DivPagerViewHolder(
    private val parentContext: BindingContext,
    private val pageLayout: DivPagerPageLayout,
    divBinder: DivBinder,
    viewCreator: DivViewCreator,
    path: DivStatePath,
    private val accessibilityEnabled: Boolean,
    private val isHorizontal: Boolean,
    private val crossAxisAlignment: Provider<ItemAlignment>,
) : DivCollectionViewHolder(pageLayout, parentContext, divBinder, viewCreator, path) {

    init {
        itemView.doOnEveryDetach { view ->
            val div = oldDiv ?: return@doOnEveryDetach
            parentContext.divView.div2Component.visibilityActionTracker
                .startTrackingViewsHierarchy(parentContext, view, div)
        }
    }

    override fun bind(bindingContext: BindingContext, div: Div, position: Int) {
        super.bind(bindingContext, div, position)

        (pageLayout.child?.layoutParams as? DivLayoutParams)
            ?.setCrossAxisAlignment(div.value(), bindingContext.expressionResolver)

        if (accessibilityEnabled) {
            pageLayout.setTag(R.id.div_pager_item_clip_id, position)
        }
    }

    private fun DivLayoutParams.setCrossAxisAlignment(div: DivBase, resolver: ExpressionResolver) {
        val childAlignment = if (isHorizontal) div.alignmentVertical else div.alignmentHorizontal
        val alignment = childAlignment?.evaluate(resolver) ?: crossAxisAlignment.get()

        gravity = if (isHorizontal) {
            when (alignment) {
                ItemAlignment.CENTER, DivAlignmentVertical.CENTER -> Gravity.CENTER
                ItemAlignment.END, DivAlignmentVertical.BOTTOM -> Gravity.BOTTOM
                else -> Gravity.TOP
            }
        } else {
            when (alignment) {
                ItemAlignment.CENTER, DivAlignmentHorizontal.CENTER -> Gravity.CENTER
                ItemAlignment.END, DivAlignmentHorizontal.END -> Gravity.END
                DivAlignmentHorizontal.LEFT -> Gravity.LEFT
                DivAlignmentHorizontal.RIGHT -> Gravity.RIGHT
                else -> Gravity.START
            }
        }

        pageLayout.requestLayout()
    }

    override fun logReuseError() = KLog.d(TAG) { "Pager holder reuse failed" }

    companion object {
        const val TAG = "DivPagerViewHolder"
    }
}
