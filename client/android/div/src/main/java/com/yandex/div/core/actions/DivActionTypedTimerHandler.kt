package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTimer
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DivActionTypedTimerHandler @Inject constructor()
    : DivActionTypedHandler {
    override fun handleAction(
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.Timer -> {
            handleTimerAction(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleTimerAction(
        action: DivActionTimer,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val id = action.id.evaluate(resolver)
        val command = action.action.evaluate(resolver).name
        view.applyTimerCommand(id, command)
    }

}
