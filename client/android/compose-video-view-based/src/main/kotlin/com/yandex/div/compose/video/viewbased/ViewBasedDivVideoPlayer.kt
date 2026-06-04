package com.yandex.div.compose.video.viewbased

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoSource
import com.yandex.div.compose.views.modifiers.onDivVisibilityChanged
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoResolution as PlayerDivVideoResolution
import com.yandex.div.core.player.DivVideoSource as PlayerDivVideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Bridges any `View`-based [DivPlayer] implementation into the Compose-native
 * [DivVideoPlayer] surface.
 *
 * Translates the delegate's callback-based [DivPlayer.Observer] into [StateFlow]
 * properties, and forwards [DivVideoPlayerConfig] updates handed to [Content] to
 * the delegate via `setSource` / `setMuted` / `setPlaybackSpeed` / `seek`.
 *
 * The underlying delegate is created lazily on the first composition of [Content]:
 * some `DivPlayer` implementations need a non-empty source list at construction time
 * to wire their internal listeners, and only at composition time do we have the real
 * config.
 */
internal class ViewBasedDivVideoPlayer(
    private val delegateFactory: DivPlayerFactory
) : DivVideoPlayer {

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isBuffering = MutableStateFlow(false)
    override val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _isEnded = MutableStateFlow(false)
    override val isEnded: StateFlow<Boolean> = _isEnded

    private val _error = MutableStateFlow<Throwable?>(null)
    override val error: StateFlow<Throwable?> = _error

    private val _currentTimeMs = MutableStateFlow(0L)
    override val currentTimeMs: StateFlow<Long> = _currentTimeMs

    private var delegate: DivPlayer? = null

    private var playerView: DivPlayerView? = null
    private var visibleOnScreen = false

    private val stateObserver = object : DivPlayer.Observer {
        override fun onReady() {
            _isReady.value = true
            _isBuffering.value = false
        }

        override fun onPlay() {
            _isPlaying.value = true
            _isEnded.value = false
        }

        override fun onPause() {
            _isPlaying.value = false
        }

        override fun onBuffering() {
            _isBuffering.value = true
        }

        override fun onEnd() {
            _isPlaying.value = false
            _isEnded.value = true
        }

        override fun onFatal(error: Throwable) {
            _error.value = error
        }

        override fun onCurrentTimeChange(timeMs: Long) {
            _currentTimeMs.value = timeMs
        }
    }

    override fun play() {
        delegate?.play()
    }

    override fun pause() {
        delegate?.pause()
    }

    override fun seek(timeMs: Long) {
        delegate?.seek(timeMs)
    }

    private fun setVisibleOnScreen(visible: Boolean) {
        visibleOnScreen = visible
        playerView?.setVisibleOnScreen(visible)
    }

    override fun release() {
        delegate?.let {
            it.removeObserver(stateObserver)
            it.release()
            delegate = null
        }
    }

    @Composable
    override fun Content(config: DivVideoPlayerConfig, modifier: Modifier) {
        val player = remember {
            delegateFactory.makePlayer(
                config.sources.toPlayerSources(),
                config.toPlayerPlaybackConfig(),
            ).also {
                it.addObserver(stateObserver)
                delegate = it
            }
        }

        LaunchedEffect(config.sources, config.autoplay, config.repeatable, config.payload) {
            player.setSource(config.sources.toPlayerSources(), config.toPlayerPlaybackConfig())
        }
        LaunchedEffect(config.muted) { player.setMuted(config.muted) }
        LaunchedEffect(config.playbackSpeed) { player.setPlaybackSpeed(config.playbackSpeed) }

        val onVisibilityChanged = remember {
            { visible: Boolean -> setVisibleOnScreen(visible) }
        }

        AndroidView(
            modifier = modifier.onDivVisibilityChanged(
                minFractionVisible = 0.01f,
                onVisibilityChanged = onVisibilityChanged,
            ),
            factory = { ctx ->
                delegateFactory.makePlayerView(ctx).apply {
                    attach(player)
                    setScale(config.scale)
                    setVisibleOnScreen(visibleOnScreen)
                    playerView = this
                }
            },
            update = { view ->
                playerView = view
                view.setScale(config.scale)
                view.setVisibleOnScreen(visibleOnScreen)
                if (view.getAttachedPlayer() !== player) {
                    view.detach()
                    view.attach(player)
                }
            },
            onRelease = { view ->
                view.detach()
                if (playerView === view) playerView = null
            }
        )
    }
}

private fun List<DivVideoSource>.toPlayerSources(): List<PlayerDivVideoSource> = map { source ->
    PlayerDivVideoSource(
        url = source.url,
        mimeType = source.mimeType,
        resolution = source.resolution?.let { PlayerDivVideoResolution(it.width, it.height) },
        bitrate = source.bitrate,
    )
}

private fun DivVideoPlayerConfig.toPlayerPlaybackConfig(): DivPlayerPlaybackConfig =
    DivPlayerPlaybackConfig(
        autoplay = autoplay,
        isMuted = muted,
        repeatable = repeatable,
        payload = payload,
        playbackSpeed = playbackSpeed,
    )
