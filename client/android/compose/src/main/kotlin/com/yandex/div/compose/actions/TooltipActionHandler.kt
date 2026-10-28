package com.yandex.div.compose.actions

import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivActionHideTooltip
import com.yandex.div2.DivActionShowTooltip
import javax.inject.Inject

internal class TooltipActionHandler @Inject constructor(
    private val tooltipStateStorage: TooltipStateStorage
) {

    fun handle(context: DivActionHandlingContext, action: DivActionShowTooltip) {
        val id = action.id.evaluate(context.expressionResolver)
        val multiple = action.multiple?.evaluate(context.expressionResolver) ?: false
        tooltipStateStorage.show(id, multiple = multiple)
    }

    fun handle(context: DivActionHandlingContext, action: DivActionHideTooltip) {
        val id = action.id.evaluate(context.expressionResolver)
        tooltipStateStorage.hide(id)
    }

    fun handle(action: DivUntypedAction.ShowTooltip) {
        tooltipStateStorage.show(action.id, multiple = action.isMultiple)
    }

    fun handle(action: DivUntypedAction.HideTooltip) {
        tooltipStateStorage.hide(action.id)
    }
}
