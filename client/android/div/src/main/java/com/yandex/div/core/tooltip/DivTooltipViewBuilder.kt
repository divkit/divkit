package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_GRAVITY
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivTooltipViewBuilder @Inject constructor(
    private val div2Builder: Provider<Div2Builder>,
) {

    fun buildTooltipView(
        tooltipData: TooltipData,
        bringToTopView: View? = null,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT,
    ): DivTooltipContainer {
        val divView = tooltipData.divView
        val tooltipContainer = DivTooltipContainer(divView.getContext())

        val substrateView = tooltipData.substrateBlock?.let { prepareView(it, divView) }
        val preparedBringToTopView = bringToTopView?.let { prepareBringToTopView(it, divView) }
        val tooltipView = prepareView(tooltipData.tooltipBlock, divView)

        tooltipContainer.setViews(
            substrate = substrateView,
            bringToTop = preparedBringToTopView,
            tooltip = tooltipView
        )

        tooltipContainer.layoutParams = ViewGroup.LayoutParams(width, height)
        return tooltipContainer
    }

    private fun prepareBringToTopView(bringToTopView: View, divView: Div2View): View? {
        val divBlock = bringToTopView.divBlock ?: return null
        return prepareView(divBlock, divView)
            .apply { makeNonInteractive() }
    }

    private fun prepareView(divBlock: DivBlock, divView: Div2View): View {
        return div2Builder.get().buildView(divBlock, divView)
            .apply { prepare(divBlock.div.value(), divBlock.expressionResolver) }
    }

    private fun View.prepare(div: DivBase, resolver: ExpressionResolver) {
        val displayMetrics = context.resources.displayMetrics
        layoutParams = ((layoutParams as? DivLayoutParams) ?: DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
            width = div.width.toLayoutParamsSize(displayMetrics, resolver, this)
            height = div.height.toLayoutParamsSize(displayMetrics, resolver, this)
            gravity = DEFAULT_GRAVITY
        }
        isFocusable = true
    }
}

private fun View.makeNonInteractive() {
    isEnabled = false
    isClickable = false
    isFocusable = true
    isFocusableInTouchMode = false
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_NONE

    if (this is ViewGroup) {
        children.forEach { it.makeNonInteractive() }
    }
}
