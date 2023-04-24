package com.yandex.div.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.yandex.div.core.ObserverList
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoPauseReason
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
    private val mediaDataSourceFactory by lazy {
        DefaultDataSourceFactory(context)
    }
    private val hlsFactory by lazy {
        HlsMediaSource.Factory(mediaDataSourceFactory)
    }
    private val fileFactory by lazy {
        ProgressiveMediaSource.Factory(mediaDataSourceFactory)
    }

    private val observers = ObserverList<DivPlayer.Observer>()
    private val updateTimeHandler = Handler(Looper.getMainLooper())
    private var currentSource: DivVideoSource? = null

    private var targetResolutionArea = 0

    private val playerPauseListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                observers.forEach { it.onResume() }
                startUpdatingPlaybackTime()
            } else {
                observers.forEach { it.onPause(DivVideoPauseReason.PAUSE_CLICKED) }
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            val case = when (state) {
                Player.STATE_BUFFERING -> DivVideoPauseReason.BUFFER_OVER
                Player.STATE_ENDED -> DivVideoPauseReason.VIDEO_OVER
                else -> null
            }
            case?.let {
                observers.forEach { observer ->
                    observer.onPause(case)
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            observers.forEach {
                it.onPause(DivVideoPauseReason.ERROR)
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
    }

    private fun updatePlaybackTime() {
        observers.forEach {
            it.onCurrentTimeUpdate(player.currentPosition)
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
            val mediaItem = MediaItem.Builder()
                .setMimeType(source.mimeType)
                .setUri(source.url)
                .build()
            val factory = when (source) {
                is DivVideoSource.StreamVideoSource -> hlsFactory
                is DivVideoSource.FileVideoSource -> fileFactory
            }
            val mediaSource = factory.createMediaSource(mediaItem)

            player.setMediaSource(mediaSource)
            player.prepare()
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
    }
}
