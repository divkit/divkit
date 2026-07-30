package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

internal class PerformActionSpan(
    val actions: List<DivAction>,
    private val resolver: ExpressionResolver,
    private val divView: Div2View,
) : ClickableSpan() {

    private val actionBinder
        get() = divView.div2Component.actionBinder

    override fun onClick(view: View) = actionBinder.handleTapClick(view, actions, resolver, divView)

    override fun updateDrawState(paint: TextPaint) = Unit
}
