package com.yandex.div.core.player

/**
 * Initial player parameters.
 */
data class DivVideoPlaybackConfig(
    val autoplay: Boolean,
    val isMuted: Boolean,
    val volume: Float,
    val startPosition: Long
)
