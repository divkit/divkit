package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.core.view2.divs.widgets.DivViewVisitor
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivCustom

internal class DivAccessibilityVisitor(
        private val divAccessibilityBinder: DivAccessibilityBinder,
        private val divView: Div2View,
        private val resolver: ExpressionResolver
) : DivViewVisitor() {
    override fun visit(view: DivWrapLayout) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivFrameLayout) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivGifImageView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivGridLayout) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivImageView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivLinearLayout) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivLineHeightTextView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivPagerIndicatorView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivPagerView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivRecyclerView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivSeparatorView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivStateLayout) = updateAccessibilityMode(view, view.divState)

    override fun visit(view: DivSliderView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivVideoView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: View) {
        val divCustom: DivCustom? = view.getTag(R.id.div_custom_tag) as? DivCustom
        if (divCustom != null) {
            updateAccessibilityMode(view, divCustom)
        }
    }

    private fun updateAccessibilityMode(view: View, div: DivBase?) {
        if (div == null) return
        divAccessibilityBinder.bindAccessibilityMode(view, divView, div.accessibility.mode.evaluate(resolver))
    }
}
