package com.yandex.div.video

import android.content.Context
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoSource

class ExoDivPlayerFactory(
    private val context: Context
) : DivPlayerFactory {
    override fun makePlayer(src: List<DivVideoSource>, config: DivPlayerPlaybackConfig): DivPlayer {
        return ExoDivPlayer(context, src, config)
    }

    override fun makePlayerView(context: Context): DivPlayerView = ExoDivPlayerView(context)

    @Deprecated("Will be removed in future releases", ReplaceWith("makePlayerView(context: Context)"))
    override fun makePlayerView(context: Context, additional: Map<String, Any>): DivPlayerView = makePlayerView(context)
}
