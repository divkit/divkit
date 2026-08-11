package com.yandex.div.compose.video.viewbased

import android.net.Uri
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.player.DivPlayerPreloader
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Wraps a callback-based [DivPlayerPreloader] into the coroutine-friendly [DivVideoPreloader].
 *
 * Usage:
 * ```
 * val factory = ViewBasedDivVideoPlayerFactory(ExoDivPlayerFactory(context))
 * val configuration = DivConfiguration(
 *     playerFactory = factory,
 *     videoPreloader = factory.makePreloader(),
 *     ...
 * )
 * ```
 */
class ViewBasedDivVideoPreloader(
    private val delegate: DivPlayerPreloader,
) : DivVideoPreloader {

    override suspend fun preloadVideo(sources: List<Uri>) {
        if (sources.isEmpty()) return
        suspendCancellableCoroutine { cont ->
            val ref = delegate.preloadVideo(sources) { _ ->
                if (cont.isActive) cont.resume(Unit)
            }
            cont.invokeOnCancellation { ref.cancel() }
        }
    }
}
