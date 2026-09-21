package com.yandex.div.compose.video

import android.net.Uri
import com.yandex.div.compose.preload.PreloadResult

/**
 * Suspend-based interface for preloading video resources before playback.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
interface DivVideoPreloader {
    /**
     * Preloads the requested video resources.
     */
    @Deprecated(
        message = "Use preloadVideoWithResult instead.",
        replaceWith = ReplaceWith("preloadVideoWithResult(sources)"),
    )
    suspend fun preloadVideo(sources: List<Uri>): Unit = Unit

    /**
     * @return result of preloading requested video resources.
     */
    @Suppress("DEPRECATION")
    suspend fun preloadVideoWithResult(sources: List<Uri>): PreloadResult {
        preloadVideo(sources)
        return PreloadResult(true)
    }
}
