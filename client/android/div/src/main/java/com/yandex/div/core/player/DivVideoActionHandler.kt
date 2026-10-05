package com.yandex.div.core.player

import com.yandex.div.core.actions.actionTargetBlockLocator
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor() {

    fun handleAction(
        div2View: Div2View,
        divId: String,
        scopeId: String?,
        action: String,
        resolver: ExpressionResolver = div2View.expressionResolver,
    ): Boolean {
        val state = when (action) {
            START_COMMAND -> DivVideoPlaybackState.PLAYING
            PAUSE_COMMAND -> DivVideoPlaybackState.PAUSED
            else -> {
                KAssert.fail { "No such video action: $action" }
                return false
            }
        }

        val target = div2View.actionTargetBlockLocator()?.findTarget(
            divId = divId,
            scopeId = scopeId,
            actionResolver = resolver,
            matches = { it is DivBlock.Video },
            reportWarning = div2View::logWarning,
        )?.onFailure { div2View.logActionError("video", it) }?.getOrNull() ?: return false

        div2View.viewStateStore.put(target.path.fullPath, DivVideoViewState(state))
        return true
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}
