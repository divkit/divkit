package com.yandex.div.core.actions

import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionFocusElement
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedFocusElementHandler @Inject constructor() : DivActionTypedHandler {
    override fun handleAction(action: DivActionTyped, view: Div2View, resolver: ExpressionResolver): Boolean {
        return when (action) {
            is DivActionTyped.FocusElement -> {
                handleRequestFocus(action.value, view, resolver)
                true
            }

            else -> false
        }
    }

    private fun handleRequestFocus(action: DivActionFocusElement, view: Div2View, resolver: ExpressionResolver) {
        val elementId = action.elementId.evaluate(resolver)
        val requestedView: View = view.findViewWithTag(elementId) ?: return
        requestedView.requestFocus()
        when (requestedView) {
            is DivInputView -> requestedView.openKeyboard()
        }
    }
}
