package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout.LayoutParams
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.divs.toLayoutParamsSize
import com.yandex.div2.Div
import javax.inject.Inject
import javax.inject.Provider

@Mockable
@DivScope
internal class DivTooltipViewBuilder @Inject constructor(
    private val div2Builder: Provider<Div2Builder>,
) {

    fun buildTooltipView(
        context: BindingContext,
        div: Div,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT,
    ): DivTooltipContainer {
        val tooltipView = prepareTooltipView(context, div)
        return DivTooltipContainer(context.divView.getContext()).apply {
            addView(tooltipView)
            layoutParams = ViewGroup.LayoutParams(width, height)
        }
    }

    private fun prepareTooltipView(
        context: BindingContext,
        div: Div,
    ): View {
        val divBase = div.value()
        val tooltipView = div2Builder.get().buildView(div, context, DivStatePath.fromState(0))
        val resolver = context.expressionResolver
        val displayMetrics = tooltipView.context.resources.displayMetrics
        tooltipView.layoutParams = LayoutParams(
            divBase.width.toLayoutParamsSize(displayMetrics, resolver),
            divBase.height.toLayoutParamsSize(displayMetrics, resolver),
        )
        tooltipView.isFocusable = true
        return tooltipView
    }
}
