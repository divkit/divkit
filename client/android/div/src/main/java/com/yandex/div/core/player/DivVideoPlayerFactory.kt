package com.yandex.div.core.player

import android.net.Uri

/**
 * Factory for [DivVideoPlayer].
 */
interface DivVideoPlayerFactory {
    fun makePlayer(src: Uri, config: DivVideoPlaybackConfig): DivVideoPlayer

    companion object {
        @JvmField
        val STUB: DivVideoPlayerFactory = object : DivVideoPlayerFactory {
            override fun makePlayer(src: Uri, config: DivVideoPlaybackConfig) = object : DivVideoPlayer { }
        }
    }
}
