package com.yandex.div.compose.video

import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Factory that produces a [DivVideoPlayer].
 *
 * The returned player applies configuration when its [DivVideoPlayer.Content] composable
 * receives a [DivVideoPlayerConfig]; the factory itself doesn't take any input.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
fun interface DivVideoPlayerFactory {

    fun makePlayer(): DivVideoPlayer
}
