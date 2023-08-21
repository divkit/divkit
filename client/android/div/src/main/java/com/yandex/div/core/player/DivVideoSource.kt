package com.yandex.div.core.player

import android.net.Uri

/**
 * Describes the content of each source sent to the player.
 */
data class DivVideoSource(
    val url: Uri,
    val mimeType: String = "",
    val resolution: DivVideoResolution? = null,
    val bitrate: Long? = null,
)
