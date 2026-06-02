package com.yandex.div.core.player

import com.yandex.div.core.actions.findTargetView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.KAssert
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor() {

    fun handleAction(
        div2View: Div2View,
        divId: String,
        scopeId: String?,
        action: String,
    ): Boolean {
        val videoView = div2View.findTargetView<DivVideoView>(divId, "video", scopeId) ?: return false
        val player = videoView.getPlayerView()?.getAttachedPlayer() ?: return false

        when (action) {
            START_COMMAND -> player.play()
            PAUSE_COMMAND -> player.pause()
            else -> {
                KAssert.fail { "No such video action: $action" }
                return false
            }
        }
        return true
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}
