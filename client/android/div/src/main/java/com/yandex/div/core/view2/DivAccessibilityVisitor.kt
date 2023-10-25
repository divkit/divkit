package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivViewVisitor
import com.yandex.div.json.expressions.ExpressionResolver

internal class DivAccessibilityVisitor(
        private val divAccessibilityBinder: DivAccessibilityBinder,
        private val divView: Div2View,
        private val resolver: ExpressionResolver
) : DivViewVisitor() {
    override fun defaultVisit(view: DivHolderView<*>) {
        view.div?.let {
            divAccessibilityBinder.bindAccessibilityMode(
                view as View,
                divView,
                it.accessibility.mode.evaluate(resolver)
            )
        }
    }
}
