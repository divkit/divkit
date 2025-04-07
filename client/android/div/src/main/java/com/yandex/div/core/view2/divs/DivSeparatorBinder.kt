package com.yandex.div.core.view2.divs

import android.view.Gravity
import com.yandex.div.R
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.Div
import com.yandex.div2.DivSeparator
import javax.inject.Inject

@DivScope
internal class DivSeparatorBinder @Inject constructor(
    baseBinder: DivBaseBinder
) : DivViewBinder<Div.Separator, DivSeparator, DivSeparatorView>(baseBinder) {

    override fun DivSeparatorView.bind(bindingContext: BindingContext, div: DivSeparator, oldDiv: DivSeparator?) {
        applyDivActions(
            bindingContext,
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.accessibility,
            div.captureFocusOnAction,
        )

        bindStyle(div.delimiterStyle, oldDiv?.delimiterStyle, bindingContext.expressionResolver)

        setDividerHeightResource(R.dimen.div_separator_delimiter_height)
        dividerGravity = Gravity.CENTER
    }

    private fun DivSeparatorView.bindStyle(
        newStyle: DivSeparator.DelimiterStyle?,
        oldStyle: DivSeparator.DelimiterStyle?,
        resolver: ExpressionResolver
    ) {
        if (newStyle?.color.equalsToConstant(oldStyle?.color)
            && newStyle?.orientation.equalsToConstant(oldStyle?.orientation)) {
            return
        }

        applyStyle(newStyle, resolver)

        if (newStyle?.color.isConstantOrNull() && newStyle?.orientation.isConstantOrNull() ) {
            return
        }

        val callback = { _: Any -> applyStyle(newStyle, resolver) }
        addSubscription(newStyle?.color?.observe(resolver, callback))
        addSubscription(newStyle?.orientation?.observe(resolver, callback))
    }

    private fun DivSeparatorView.applyStyle(style: DivSeparator.DelimiterStyle?, resolver: ExpressionResolver) {
        if (style == null) {
            dividerColor = DivSeparatorView.DEFAULT_DIVIDER_COLOR
            isHorizontal = true
        } else {
            dividerColor = style.color.evaluate(resolver)
            val orientation = style.orientation.evaluate(resolver)
            isHorizontal = orientation == DivSeparator.DelimiterStyle.Orientation.HORIZONTAL
        }
    }
}
