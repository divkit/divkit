package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivAnimatorTypedActionHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        return when (action) {
            is DivActionTyped.AnimatorStart -> {
                if (scopeId == null) return true
                val scopeViews = ViewLocator.findViewsWithTag(view, scopeId)
                if (scopeViews.size != 1) return true

                val targetView = scopeViews.first()
                view.viewComponent.animatorController.startAnimator(scopeId, targetView, action.value, resolver)
                true
            }

            is DivActionTyped.AnimatorStop -> {
                if (scopeId == null) return true
                view.viewComponent.animatorController.stopAnimator(scopeId, action.value.animatorId)
                true
            }

            else -> false
        }
    }
}
