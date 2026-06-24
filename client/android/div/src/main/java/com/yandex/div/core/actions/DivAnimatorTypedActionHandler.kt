package com.yandex.div.core.actions

import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.bindingContext
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionAnimatorStart
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
                val targetView = view.findTargetView<View>(scopeId, DivActionAnimatorStart.TYPE) ?: return true
                val targetResolver = targetView.bindingContext?.expressionResolver ?: return true
                view.viewComponent.animatorController.startAnimator(scopeId, targetView, action.value, targetResolver)
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
