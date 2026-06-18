package com.yandex.div.compose.video

import android.net.Uri
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Suspend-based interface for preloading video resources before playback.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface DivVideoPreloader {
    suspend fun preloadVideo(sources: List<Uri>)
}
