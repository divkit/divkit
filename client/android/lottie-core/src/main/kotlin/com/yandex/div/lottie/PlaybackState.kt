package com.yandex.div.lottie

sealed interface PlaybackState {
    data object Idle : PlaybackState

    data class Started(val sequence: Long) : PlaybackState

    data class Stopped(val sequence: Long) : PlaybackState

    data class Completed(val sequence: Long) : PlaybackState
}
