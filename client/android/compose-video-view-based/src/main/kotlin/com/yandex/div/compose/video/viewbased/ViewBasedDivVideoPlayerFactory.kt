package com.yandex.div.compose.video.viewbased

import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.player.DivPlayerFactory

/**
 * Compose-native adapter that exposes any `View`-based [DivPlayerFactory] (e.g. the
 * media3 ExoPlayer factory or the Yandex `ListYandexPlayer` factory) through the
 * Compose-native [DivVideoPlayerFactory] surface.
 *
 * The produced [DivVideoPlayer] renders itself via [androidx.compose.ui.viewinterop.AndroidView]
 * interop wrapping the underlying [com.yandex.div.core.player.DivPlayerView]. A future
 * pure-Compose implementation would use a Compose-native player surface instead.
 *
 * Usage:
 * ```
 * val configuration = DivComposeConfiguration(
 *     playerFactory = ViewBasedDivVideoPlayerFactory(ExoDivPlayerFactory(context)),
 *     ...
 * )
 * ```
 */
@ExperimentalApi
class ViewBasedDivVideoPlayerFactory(
    private val delegateFactory: DivPlayerFactory,
) : DivVideoPlayerFactory {
    override fun makePlayer(): DivVideoPlayer = ViewBasedDivVideoPlayer(delegateFactory)
}
