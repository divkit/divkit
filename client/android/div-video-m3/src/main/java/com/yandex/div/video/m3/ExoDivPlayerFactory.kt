package com.yandex.div.video.m3

import android.content.Context
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.network.DivNetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

public class ExoDivPlayerFactory(
    private val context: Context,
    networkClient: DivNetworkClient?,
    networkScope: CoroutineScope,
) : DivPlayerFactory {
    public constructor(context: Context) : this(context, null, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    public constructor(context: Context, networkClient: DivNetworkClient?) :
        this(context, networkClient, CoroutineScope(SupervisorJob() + Dispatchers.IO))

    private val cache = ExoPlayerCache(context, networkClient, networkScope)

    override fun makePlayer(src: List<DivVideoSource>, config: DivPlayerPlaybackConfig): DivPlayer {
        return ExoDivPlayer(context, src, config, cache.cacheDataSourceFactory)
    }

    override fun makePlayerView(context: Context): DivPlayerView = ExoDivPlayerView(context)

    override fun makePreloader(): DivPlayerPreloader {
        return ExoPlayerVideoPreloader(context, cache)
    }
}
