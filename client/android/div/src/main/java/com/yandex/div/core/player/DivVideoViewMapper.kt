package com.yandex.div.core.player

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div2.DivVideo
import java.util.WeakHashMap
import javax.inject.Inject

@DivScope
internal class DivVideoViewMapper @Inject constructor() {
    private val currentViews = WeakHashMap<DivVideo, DivVideoView>()

    fun addView(view: DivVideoView, div: DivVideo) {
        currentViews[div] = view
    }

    fun getPlayerView(div: DivVideo): DivPlayerView? {
        val playerView = currentViews[div]?.getPlayerView()

        if (playerView == null) {
            currentViews.remove(div)
        }
        return playerView
    }
}
