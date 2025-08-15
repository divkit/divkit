package com.yandex.div.video

import android.content.Context
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoSource

public class ExoDivPlayerFactory(
    private val context: Context
) : DivPlayerFactory {
    private val cache = ExoPlayerCache(context)

    override fun makePlayer(src: List<DivVideoSource>, config: DivPlayerPlaybackConfig): DivPlayer {
        return ExoDivPlayer(context, src, config, cache.cacheDataSourceFactory)
    }

    override fun makePlayerView(context: Context): DivPlayerView = ExoDivPlayerView(context)

    override fun makePreloader(): DivPlayerPreloader {
        return ExoPlayerVideoPreloader(context, cache)
    }
}
