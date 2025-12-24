package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.children
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.divs.asDivHolderView
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import javax.inject.Inject
import javax.inject.Provider
import kotlin.sequences.forEach

@Mockable
@DivScope
internal class DivTooltipViewBuilder @Inject constructor(
    private val div2Builder: Provider<Div2Builder>,
) {

    fun buildTooltipView(
        context: BindingContext,
        divTooltip: DivTooltip,
        bringToTopView: View? = null,
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT,
    ) = DivTooltipContainer(context.divView.getContext()).apply {
        val substrateView = divTooltip.substrateDiv?.let { substrateDiv ->
            prepareView(context, substrateDiv)
        }
        
        val preparedBringToTopView =  bringToTopView?.let { bringToTopView ->
            prepareBringToTopView(context, bringToTopView)
        }
        
        val tooltipView = prepareView(context, divTooltip.div)
        
        setViews(
            substrate = substrateView,
            bringToTop = preparedBringToTopView,
            tooltip = tooltipView
        )

        layoutParams = ViewGroup.LayoutParams(width, height)
    }

    private fun prepareBringToTopView(
        context: BindingContext,
        bringToTopView: View,
    ) = bringToTopView.asDivHolderView?.div?.let { div ->
        prepareView(context, div).apply { makeNonInteractive() }
    }

    private fun prepareView(
        context: BindingContext,
        div: Div,
    ) = div2Builder.get().buildView(
        data = div,
        context = context,
        path = DivStatePath.fromRootDiv(0, div),
    ).apply {
        val divBase = div.value()
        val resolver = context.expressionResolver
        val displayMetrics = this.context.resources.displayMetrics
        layoutParams = ViewGroup.LayoutParams(
            divBase.width.toLayoutParamsSize(displayMetrics, resolver),
            divBase.height.toLayoutParamsSize(displayMetrics, resolver),
        )
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
