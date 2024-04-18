package com.yandex.div.core.player

import android.content.Context

/**
 * Factory for [DivPlayer].
 */
interface DivPlayerFactory {
    fun makePlayer(src: List<DivVideoSource>, config: DivPlayerPlaybackConfig): DivPlayer

    fun makePlayerView(context: Context): DivPlayerView

    @Deprecated("Will be removed in future releases", ReplaceWith("makePlayerView(context: Context)"))
    fun makePlayerView(context: Context, additional: Map<String, Any>): DivPlayerView = makePlayerView(context)

    companion object {
        @JvmField
        val STUB: DivPlayerFactory = object : DivPlayerFactory {
            override fun makePlayer(src: List<DivVideoSource>, config: DivPlayerPlaybackConfig) = object : DivPlayer { }

            override fun makePlayerView(context: Context) = object : DivPlayerView(context) { }

            @Deprecated("Will be removed in future releases", ReplaceWith("makePlayerView(context: Context)"))
            override fun makePlayerView(context: Context, additional: Map<String, Any>) = object : DivPlayerView(context) { }
        }
    }
}
