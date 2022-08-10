package com.yandex.divkit.demo.div.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.yandex.div.video.custom.ImageCache
import java.io.File

private const val DEFAULT_CAPACITY = 30
private const val IMAGE_CACHE_DIR = "demo_image_cache"

/**
 * Simple cache for images.
 * Keeps at most [capacity] bitmaps and evicts those that were not modified for the longest time.
 */
class DemoVideoCustomImageCache(
    context: Context,
    private val capacity: Int = DEFAULT_CAPACITY,
    private val cacheDir: File = File(context.externalCacheDir, IMAGE_CACHE_DIR)
) : ImageCache {

    override fun getImageBitmap(url: String): Bitmap? {
        val cachedFile = getCachedFile(url)

        if (!cachedFile.exists()) {
            return null
        }
        return BitmapFactory.decodeStream(cachedFile.inputStream())
    }

    override fun storeImage(url: String, bitmap: Bitmap) {
        val cachedFile = getCachedFile(url)

        cacheDir.mkdirs()
        cachedFile.createNewFile()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, cachedFile.outputStream())

        trimToSize()
    }

    private fun trimToSize() {
        val cachedFiles = cacheDir.listFiles()
            ?: throw IllegalArgumentException("Cache dir $cacheDir is invalid")

        val needToDelete = cachedFiles.size - capacity
        if (needToDelete <= 0) {
            return
        }
        cachedFiles
            .sortedBy { it.lastModified() }
            .take(needToDelete)
            .forEach { it.delete() }
    }

    private fun getCachedFile(url: String): File {
        val key = Integer.toHexString(url.hashCode())
        return File(cacheDir, key)
    }
}
