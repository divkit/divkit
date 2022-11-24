package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.applyDivActions
import com.yandex.div.core.view2.divs.widgets.*
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
import com.yandex.div.core.view2.divs.widgets.DivSnappyRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivViewVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.*
import javax.inject.Inject

internal class DivAccessibilityVisitor(
        private val divAccessibilityBinder: DivAccessibilityBinder,
        private val divView: Div2View,
        private val resolver: ExpressionResolver
) : DivViewVisitor() {
    override fun visit(view: DivWrapLayout) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivFrameLayout) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivGifImageView) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivGridLayout) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivImageView) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivLinearLayout) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivLineHeightTextView) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivPagerIndicatorView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivPagerView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivRecyclerView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivSeparatorView) {
        val div = view.div ?: return
        updateAccessibilityMode(view, div)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)
    }

    override fun visit(view: DivSnappyRecyclerView) = updateAccessibilityMode(view, view.div)

    override fun visit(view: DivStateLayout) = updateAccessibilityMode(view, view.divState)

    override fun visit(view: DivSliderView) = updateAccessibilityMode(view, view.div)

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
