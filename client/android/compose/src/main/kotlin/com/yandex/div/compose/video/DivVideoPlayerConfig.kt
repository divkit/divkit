package com.yandex.div.compose.video

import android.net.Uri
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div2.DivVideoScale
import org.json.JSONObject

/**
 * Input parameters supplied to a [DivVideoPlayer] via [DivVideoPlayer.Content].
 *
 * Fed into the player on every composition; the player observes changes between
 * subsequent values and reacts (e.g. starts a new source, mutes, changes speed).
 */
@ExperimentalApi
data class DivVideoPlayerConfig(
    val sources: List<DivVideoSource> = emptyList(),
    val autoplay: Boolean = false,
    val repeatable: Boolean = false,
    val payload: JSONObject? = null,
    val muted: Boolean = false,
    val playbackSpeed: Float = 1f,
    val scale: DivVideoScale = DivVideoScale.FIT,
)

/**
 * Single source variant passed to a [DivVideoPlayer].
 */
@ExperimentalApi
data class DivVideoSource(
    val url: Uri,
    val mimeType: String = "",
    val resolution: DivVideoResolution? = null,
    val bitrate: Long? = null,
)

/**
 * Optional resolution metadata for a [DivVideoSource].
 */
@ExperimentalApi
data class DivVideoResolution(
    val width: Int,
    val height: Int,
)
