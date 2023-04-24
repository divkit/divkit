package com.yandex.div.core.player

import android.net.Uri

/**
 * Describes the content of each source sent to the player.
 */
sealed class DivVideoSource(
    val url: Uri,
    val codec: String? = null,
    val mimeType: String? = null,
    val resolution: DivVideoResolution? = null
) {
    class StreamVideoSource(url: Uri) : DivVideoSource(url)
    class FileVideoSource(
        url: Uri,
        codec: String? = null,
        mimeType: String? = null,
        resolution: DivVideoResolution? = null
    ) : DivVideoSource(url, codec, mimeType, resolution)
}
