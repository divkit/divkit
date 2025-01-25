package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout.ALIGN_PARENT_LEFT
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import android.widget.RelativeLayout.LayoutParams
import com.yandex.div.core.actions.logError
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.toLayoutParamsSize
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject
import javax.inject.Provider

@Mockable
@DivScope
internal class DivTooltipViewBuilder @Inject constructor(
    private val div2Builder: Provider<Div2Builder>,
) {

    fun buildTooltipView(
        div: Div,
        div2View: Div2View,
        context: BindingContext,
        resolver: ExpressionResolver
    ): DivTooltipContainer? {
        val tooltipView = prepareTooltipView(div, div2View, context, resolver) ?: return null

        return DivTooltipContainer(context.divView.getContext(), tooltipView).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            isClickable = true
            isFocusable = true
        }
    }

    private fun prepareTooltipView(
        div: Div,
        div2View: Div2View,
        context: BindingContext,
        resolver: ExpressionResolver
    ): View? {
        val tooltipView = div2Builder.get().buildView(div, context, DivStatePath.fromState(0))
        if (tooltipView == null) {
            Assert.fail("Broken div in popup")
            div2View.logError(AssertionError("Broken div in popup!"))
            return null
        }

        val divBase = div.value()
        val displayMetrics = tooltipView.context.resources.displayMetrics
        tooltipView.layoutParams = LayoutParams(
            divBase.width.toLayoutParamsSize(displayMetrics, resolver),
            divBase.height.toLayoutParamsSize(displayMetrics, resolver),
        ).apply {
            addRule(ALIGN_PARENT_LEFT)
            addRule(ALIGN_PARENT_TOP)
        }
        tooltipView.isFocusable = true
        return tooltipView
    }
}
