package com.yandex.div.core.actions

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PathFormatException
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionSetState
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedSetStateHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.SetState -> {
            handleSetState(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleSetState(
        action: DivActionSetState,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val stateId = action.stateId.evaluate(resolver)
        val temporary = action.temporary.evaluate(resolver)
        val statePath = try {
            DivStatePath.parse(stateId)
        } catch (e: PathFormatException) {
            view.logError(IllegalArgumentException("Invalid format of $stateId", e))
            return
        }
        view.switchToState(statePath, temporary)
    }

}
