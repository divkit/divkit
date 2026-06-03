package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionShowTooltip
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedShowTooltipHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.ShowTooltip -> {
            handleShowTooltip(action.value, scopeId, view, resolver)
            true
        }
        else -> false
    }

    private fun handleShowTooltip(
        action: DivActionShowTooltip,
        scopeId: String?,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val tooltipId = action.id.evaluate(resolver)
        val multiple = action.multiple?.evaluate(resolver) ?: false
        view.showTooltip(tooltipId, multiple, scopeId)
    }
}
