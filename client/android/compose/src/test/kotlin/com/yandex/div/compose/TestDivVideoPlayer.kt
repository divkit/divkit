package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoPlayerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestDivVideoPlayerFactory : DivVideoPlayerFactory {

    var lastCreatedPlayer: TestDivVideoPlayer? = null
        private set

    override fun makePlayer(): DivVideoPlayer {
        return TestDivVideoPlayer().also { lastCreatedPlayer = it }
    }
}

/**
 * Records seek requests; tests drive the position through [currentTimeMsState]. [seek] moves the
 * position immediately, clamped to [durationMs], unless [acknowledgeSeek] is `false`.
 */
class TestDivVideoPlayer : DivVideoPlayer {
    val currentTimeMsState = MutableStateFlow(0L)
    val seekRequests = mutableListOf<Long>()
    var acknowledgeSeek = true
    var durationMs = Long.MAX_VALUE

    override val isReady: StateFlow<Boolean> = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = MutableStateFlow(false)
    override val isBuffering: StateFlow<Boolean> = MutableStateFlow(false)
    override val isEnded: StateFlow<Boolean> = MutableStateFlow(false)
    override val error: StateFlow<Throwable?> = MutableStateFlow(null)
    override val currentTimeMs: StateFlow<Long> = currentTimeMsState

    override fun play() = Unit

    override fun pause() = Unit

    override fun seek(timeMs: Long) {
        seekRequests.add(timeMs)
        if (acknowledgeSeek) currentTimeMsState.value = timeMs.coerceIn(0L, durationMs)
    }

    override fun release() = Unit

    @Composable
    override fun Content(config: DivVideoPlayerConfig, modifier: Modifier) = Unit
}
