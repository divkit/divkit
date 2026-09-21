package com.yandex.div.compose.video.viewbased

import android.net.Uri
import com.yandex.div.compose.preload.PreloadResult
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.CompositeResult
import com.yandex.div.core.preload.filterErrorResults
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

    @Deprecated(
        message = "Use preloadVideoWithResult instead.",
        replaceWith = ReplaceWith("preloadVideoWithResult(sources)"),
    )
    override suspend fun preloadVideo(sources: List<Uri>) {
        preloadVideoWithResult(sources)
    }

    @OptIn(InternalApi::class)
    override suspend fun preloadVideoWithResult(sources: List<Uri>): PreloadResult {
        if (sources.isEmpty()) return PreloadResult(true)
        return suspendCancellableCoroutine { cont ->
            val ref = delegate.preloadVideo(sources) { results ->
                if (cont.isActive) {
                    cont.resume(PreloadResult(
                        CompositeResult(results).filterErrorResults().firstOrNull() == null
                    ))
                }
            }
            cont.invokeOnCancellation { ref.cancel() }
        }
    }
}
