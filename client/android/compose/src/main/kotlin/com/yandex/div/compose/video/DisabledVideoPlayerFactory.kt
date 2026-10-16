package com.yandex.div.compose.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal object DisabledVideoPlayerFactory : DivVideoPlayerFactory {
    override fun makePlayer(): DivVideoPlayer = object : DivVideoPlayer {
        override val isReady: StateFlow<Boolean> = MutableStateFlow(false)
        override val isPlaying: StateFlow<Boolean> = MutableStateFlow(false)
        override val isBuffering: StateFlow<Boolean> = MutableStateFlow(false)
        override val isEnded: StateFlow<Boolean> = MutableStateFlow(false)
        override val error: StateFlow<Throwable?> = MutableStateFlow(null)
        override val currentTimeMs: StateFlow<Long> = MutableStateFlow(0L)

        override fun seek(timeMs: Long) = Unit
        override fun play() = Unit
        override fun pause() = Unit
        override fun release() = Unit

        @Composable
        override fun Content(config: DivVideoPlayerConfig, modifier: Modifier) = Unit
    }
}
