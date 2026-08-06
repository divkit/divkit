package com.yandex.div.core.view2.divs.pager

import android.view.Gravity
import com.yandex.div.core.util.doOnEveryDetach
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionViewHolder
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivPager.ItemAlignment

internal class DivPagerViewHolder(
    private val pageLayout: DivPagerPageLayout,
    divBinder: DivBinder,
    viewCreator: DivViewCreator,
    private val divView: Div2View,
    private val isHorizontal: () -> Boolean,
    private val crossAxisAlignment: () -> ItemAlignment,
) : DivCollectionViewHolder(pageLayout, divBinder, viewCreator, divView) {

    init {
        itemView.doOnEveryDetach { view ->
            val divBlock = oldDivBlock ?: return@doOnEveryDetach
            divView.div2Component.visibilityActionTracker
                .startTrackingViewsHierarchy(view, divBlock.div, divBlock.expressionResolver, divView)
        }
    }

    override fun bind(divBlock: DivBlock, position: Int) {
        super.bind(divBlock, position)
        (pageLayout.child?.layoutParams as? DivLayoutParams)?.setCrossAxisAlignment(divBlock)
    }

    private fun DivLayoutParams.setCrossAxisAlignment(divBlock: DivBlock) {
        val div = divBlock.div.value()
        val childAlignment = if (isHorizontal()) div.alignmentVertical else div.alignmentHorizontal
        val alignment = childAlignment?.evaluate(divBlock.expressionResolver) ?: crossAxisAlignment()

        gravity = if (isHorizontal()) {
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
