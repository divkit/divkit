package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.view.Gravity
import com.yandex.div.R
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSeparator
import javax.inject.Inject

@DivScope
internal class DivSeparatorBinder @Inject constructor(
    private val baseBinder: DivBaseBinder
) : DivViewBinder<DivSeparator, DivSeparatorView> {

    override fun bindView(view: DivSeparatorView, div: DivSeparator, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.applyStyle(div.delimiterStyle, expressionResolver)

        view.setDividerHeightResource(R.dimen.div_separator_delimiter_height)
        view.dividerGravity = Gravity.CENTER
    }

    private fun DivSeparatorView.applyStyle(style: DivSeparator.DelimiterStyle?, resolver: ExpressionResolver) {
        val colorExpr = style?.color
        if (colorExpr == null) {
            dividerColor = Color.TRANSPARENT
        } else {
            addSubscription(colorExpr.observeAndGet(resolver) { color -> dividerColor = color })
        }

        val orientationExpr = style?.orientation
        if (orientationExpr == null) {
            isHorizontal = false
        } else {
            addSubscription(
                orientationExpr.observeAndGet(resolver) { orientation ->
                    isHorizontal = orientation == DivSeparator.DelimiterStyle.Orientation.HORIZONTAL
                }
            )
        }
    }
}
