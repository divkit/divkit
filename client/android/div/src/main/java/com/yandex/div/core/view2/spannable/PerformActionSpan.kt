package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivAction

internal class PerformActionSpan(
    private val bindingContext: BindingContext,
    val actions: List<DivAction>
) : ClickableSpan() {

    private val actionBinder
        get() = bindingContext.divView.div2Component.actionBinder

    override fun onClick(view: View) = actionBinder.handleTapClick(bindingContext, view, actions)

    override fun updateDrawState(paint: TextPaint) = Unit
}
