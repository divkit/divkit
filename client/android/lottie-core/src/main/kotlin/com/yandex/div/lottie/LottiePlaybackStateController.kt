package com.yandex.div.lottie

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Playback requested by extension actions for one card. Every start increments its sequence. */
class LottiePlaybackStateController {

    private val mutableStates = MutableStateFlow<Map<Any, PlaybackState>>(emptyMap())

    val states: StateFlow<Map<Any, PlaybackState>> = mutableStates.asStateFlow()

    fun start(path: Any) {
        mutableStates.update { states ->
            val sequence = when (val current = states[path] ?: PlaybackState.Idle) {
                PlaybackState.Idle -> 1L
                is PlaybackState.Started -> current.sequence + 1
                is PlaybackState.Stopped -> current.sequence + 1
                is PlaybackState.Completed -> current.sequence + 1
            }
            states + (path to PlaybackState.Started(sequence))
        }
    }

    /** Stops both configured and action-driven playback until the next explicit start. */
    fun stop(path: Any) {
        mutableStates.update { states ->
            val sequence = when (val current = states[path] ?: PlaybackState.Idle) {
                PlaybackState.Idle -> 0L
                is PlaybackState.Started -> current.sequence
                is PlaybackState.Stopped -> current.sequence
                is PlaybackState.Completed -> current.sequence
            }
            states + (path to PlaybackState.Stopped(sequence))
        }
    }

    fun onPlaybackFinished(path: Any, sequence: Long) {
        mutableStates.update { states ->
            if (states[path] == PlaybackState.Started(sequence)) {
                states + (path to PlaybackState.Completed(sequence))
            } else {
                states
            }
        }
    }

    /**
     * Records completion of autoplay started by Lottie configuration, without an action sequence.
     *
     * [PlaybackState.Completed] with sequence `0` is recorded only while [path] is idle, so an
     * action-driven start or its completion is not overwritten.
     */
    fun onAutoplayed(path: Any) {
        mutableStates.update { states ->
            if ((states[path] ?: PlaybackState.Idle) == PlaybackState.Idle) {
                states + (path to PlaybackState.Completed(0))
            } else {
                states
            }
        }
    }
}
