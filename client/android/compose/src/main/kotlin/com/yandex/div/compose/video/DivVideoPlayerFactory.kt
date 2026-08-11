package com.yandex.div.compose.video

/**
 * Factory that produces a [DivVideoPlayer].
 *
 * The returned player applies configuration when its [DivVideoPlayer.Content] composable
 * receives a [DivVideoPlayerConfig]; the factory itself doesn't take any input.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
fun interface DivVideoPlayerFactory {

    fun makePlayer(): DivVideoPlayer
}
