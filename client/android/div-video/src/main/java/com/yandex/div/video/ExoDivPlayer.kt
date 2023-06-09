package com.yandex.div.video

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.yandex.div.core.ObserverList
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.internal.KAssert

class ExoDivPlayer(
    private val context: Context,
    private var src: List<DivVideoSource>,
    config: DivPlayerPlaybackConfig
) : DivPlayer {
    val player: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
    }
    private val mediaSourceAbstractFactory by lazy {
        ExoDivMediaSourceAbstractFactory(context)
    }

    private val observers = ObserverList<DivPlayer.Observer>()
    private val updateTimeHandler = Handler(Looper.getMainLooper())
    private var currentSource: DivVideoSource? = null
    private var needToRenderFrameExplicitly = false

    private var targetResolutionArea = 0

    private val playerPauseListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                observers.forEach { it.onPlay() }
                startUpdatingPlaybackTime()
            } else {
                observers.forEach { it.onPause() }
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state != Player.STATE_BUFFERING && state != Player.STATE_ENDED) return
            if (state == Player.STATE_ENDED) needToRenderFrameExplicitly = true

            observers.forEach {
                when (state) {
                    Player.STATE_BUFFERING -> it.onBuffering()
                    Player.STATE_ENDED -> it.onEnd()
                    else -> Unit
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            observers.forEach {
                it.onFatal()
            }
        }
    }

    private val playerActivityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) = Unit

        override fun onActivityStarted(activity: Activity) = Unit

        override fun onActivityDestroyed(activity: Activity) {
            (context.applicationContext as Application).unregisterActivityLifecycleCallbacks(this)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

        override fun onActivityStopped(activity: Activity) = Unit

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

        override fun onActivityResumed(activity: Activity) {
            if (needToRenderFrameExplicitly) {
                needToRenderFrameExplicitly = false
                player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                seek(player.currentPosition)
                pause()
                player.setSeekParameters(SeekParameters.EXACT)
            }
        }
    }

    init {
        if (src.isNotEmpty()) {
            player.addListener(playerPauseListener)
            setConfig(config)
        } else {
            KAssert.fail { "Attempt to create a player with an empty source" }
        }

        (context.applicationContext as Application).registerActivityLifecycleCallbacks(playerActivityCallback)
    }

    private fun updatePlaybackTime() {
        observers.forEach {
            it.onCurrentTimeChange(player.currentPosition)
        }
    }

    private fun startUpdatingPlaybackTime() {
        updateTimeHandler.removeCallbacksAndMessages(null)
        keepUpdatingPlaybackTime()
    }

    private fun keepUpdatingPlaybackTime() {
        updatePlaybackTime()

        updateTimeHandler.postDelayed({
            if (player.isPlaying) {
                keepUpdatingPlaybackTime()
            }
        }, 1000)
    }

    private fun setConfig(config: DivPlayerPlaybackConfig) {
        if (config.isMuted) player.volume = 0f
        player.repeatMode = if (config.repeatable) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
        player.playWhenReady = config.autoplay
    }

    private fun applyMediaSource() {
        var minSourceGreaterThanTarget: DivVideoSource? = null
        var minArea = 0

        src.forEach {
            if (it.resolution != null) {
                val area = it.resolution!!.width * it.resolution!!.height
                if (area in targetResolutionArea until minArea) {
                    minSourceGreaterThanTarget = it
                    minArea = area
                }
            }
        }

        currentSource = minSourceGreaterThanTarget ?: src.first()

        currentSource?.let { source ->
            mediaSourceAbstractFactory.create(source.url.toString())?.let { factory ->
                val mediaItem = MediaItem.Builder()
                    .setMimeType(source.mimeType)
                    .setUri(source.url)
                    .build()
                val mediaSource = factory.createMediaSource(mediaItem)

                player.setMediaSource(mediaSource)
                player.prepare()
            }
        }
    }

    override fun addObserver(observer: DivPlayer.Observer) {
        observers.addObserver(observer)
    }

    override fun removeObserver(observer: DivPlayer.Observer) {
        observers.removeObserver(observer)
    }

    override fun setSource(sourceVariants: List<DivVideoSource>, config: DivPlayerPlaybackConfig) {
        setConfig(config)
        src = sourceVariants
        applyMediaSource()
    }

    fun setTargetResolution(width: Int, height: Int) {
        targetResolutionArea = width * height

        applyMediaSource()
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun seek(toMs: Long) {
        player.seekTo(toMs)
        updatePlaybackTime()
    }

    override fun release() {
        player.release()
        (context.applicationContext as Application).unregisterActivityLifecycleCallbacks(playerActivityCallback)
    }
}
