package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionVideo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DivActionTypedVideoHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.Video -> {
            handleVideoAction(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleVideoAction(
        action: DivActionVideo,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val id = action.id.evaluate(resolver)
        val videoAction = action.action.evaluate(resolver).name
        view.applyVideoCommand(id, videoAction, resolver)
    }
}
