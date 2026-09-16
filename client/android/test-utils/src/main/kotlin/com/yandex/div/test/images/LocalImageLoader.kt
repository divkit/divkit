package com.yandex.div.test.images

import android.content.res.AssetManager
import android.net.Uri
import androidx.core.net.toUri
import java.io.IOException

/**
 * Provides images from `test-utils/src/main/assets/images` for screenshot tests.
 */
class LocalImageLoader(private val assetManager: AssetManager) {

    fun load(uri: Uri): ByteArray? {
        val path = assetPath(uri) ?: return null
        val stream = try {
            assetManager.open(path)
        } catch (e: IOException) {
            throw IllegalStateException(
                "Loading images from network in screenshot tests is forbidden. Local image not found: $path",
                e,
            )
        }
        return stream.use { it.readBytes() }
    }

    fun resolve(uri: Uri): Uri {
        val path = assetPath(uri) ?: return uri
        return "file:///android_asset/$path".toUri()
    }

    private fun assetPath(uri: Uri): String? {
        if (uri.scheme != "https") {
            return null
        }
        val fileName = requireNotNull(uri.lastPathSegment) { "Missing image file name: $uri" }
        return "images/$fileName"
    }
}
