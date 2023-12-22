package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.view.Gravity
import com.yandex.div.R
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div2.DivSeparator
import javax.inject.Inject

@DivScope
internal class DivSeparatorBinder @Inject constructor(
    private val baseBinder: DivBaseBinder
) : DivViewBinder<DivSeparator, DivSeparatorView> {

    override fun bindView(view: DivSeparatorView, div: DivSeparator, divView: Div2View) {
        val oldDiv = view.div
        if (div === oldDiv) return

        baseBinder.bindView(view, div, oldDiv, divView)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.bindStyle(div.delimiterStyle, oldDiv?.delimiterStyle, divView.expressionResolver)

        view.setDividerHeightResource(R.dimen.div_separator_delimiter_height)
        view.dividerGravity = Gravity.CENTER
    }

    private fun DivSeparatorView.bindStyle(
        newStyle: DivSeparator.DelimiterStyle,
        oldStyle: DivSeparator.DelimiterStyle?,
        resolver: ExpressionResolver
    ) {
        if (newStyle.color.equalsToConstant(oldStyle?.color)
            && newStyle.orientation.equalsToConstant(oldStyle?.orientation)) {
            return
        }

        applyStyle(newStyle, resolver)

        if (newStyle.color.isConstant() && newStyle.orientation.isConstant()) {
            return
        }

        val callback = { _: Any -> applyStyle(newStyle, resolver) }
        addSubscription(newStyle.color.observe(resolver, callback))
        addSubscription(newStyle.orientation.observe(resolver, callback))
    }

    private fun DivSeparatorView.applyStyle(style: DivSeparator.DelimiterStyle?, resolver: ExpressionResolver) {
        dividerColor = style?.color?.evaluate(resolver) ?: Color.TRANSPARENT
        val orientation = style?.orientation?.evaluate(resolver) ?: DivSeparator.DelimiterStyle.Orientation.VERTICAL
        isHorizontal = orientation == DivSeparator.DelimiterStyle.Orientation.HORIZONTAL
    }
}
