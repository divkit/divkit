package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionHideTooltip
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedHideTooltipHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.HideTooltip -> {
            handleHideTooltip(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleHideTooltip(
        action: DivActionHideTooltip,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val tooltipId = action.id.evaluate(resolver)
        view.hideTooltip(tooltipId)
    }
}
