package com.yandex.div.core.player

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div2.DivVideo
import java.util.WeakHashMap
import javax.inject.Inject

@DivScope
internal class DivVideoViewMapper @Inject constructor() {
    private val currentViews = WeakHashMap<DivVideoView, DivVideo>()

    fun addView(view: DivVideoView, div: DivVideo) {
        currentViews[view] = div
    }

    fun getPlayer(div: DivVideo): DivPlayer? {
        return currentViews.entries.filter { it.value == div || it.value.id == div.id }
            .mapNotNull { it.key.getPlayerView()?.getAttachedPlayer() }
            .firstOrNull()
    }
}
