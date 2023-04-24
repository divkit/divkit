package com.yandex.div.core.player

import android.view.ViewGroup
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.KAssert
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor() {
    fun handleAction(div2View: Div2View, divId: String, action: String): Boolean {
        val player = findVideoViewWithId(div2View, divId)?.getPlayerView()?.getAttachedPlayer() ?: return false

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

    private fun findVideoViewWithId(currentView: ViewGroup, id: String): DivVideoView? {
        for (i in 0 until currentView.childCount) {
            val child = currentView.getChildAt(i)
            if (child is DivVideoView && child.div?.id == id) {
                return child
            } else if (child is ViewGroup) {
                findVideoViewWithId(child, id)?.let {
                    return it
                }
            }
        }
        return null
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}
