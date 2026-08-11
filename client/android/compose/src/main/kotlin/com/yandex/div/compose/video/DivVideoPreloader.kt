package com.yandex.div.compose.video

import android.net.Uri

/**
 * Suspend-based interface for preloading video resources before playback.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
interface DivVideoPreloader {
    suspend fun preloadVideo(sources: List<Uri>)
}
