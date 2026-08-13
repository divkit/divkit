package com.yandex.div.lottie

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieImageAsset

private const val DATA_URI_PREFIX = "data:"
private const val BASE64_MARKER = "base64,"

/**
 * Decodes images embedded into the composition as base64 data URIs and attaches decoded
 * bitmaps to the corresponding [LottieImageAsset]s. Otherwise Lottie's ImageAssetManager
 * decodes such images lazily on the main thread during the first draw.
 *
 * Decoding mirrors ImageAssetManager behavior so the rendered result stays identical.
 */
internal fun LottieComposition.decodeBase64Images(logger: DivLottieLogger) {
    for (asset in images.values) {
        if (asset.bitmap != null) continue
        val bitmap = try {
            decodeBase64Image(asset, logger)
        } catch (e: Exception) {
            // Never propagate: a broken asset must not fail preload or bind,
            // it is simply left for the lazy decoding path.
            logger.fail("Failed to predecode image `${asset.id}`.", e)
            null
        } ?: continue
        asset.setBitmap(bitmap)
    }
}

private fun decodeBase64Image(asset: LottieImageAsset, logger: DivLottieLogger): Bitmap? {
    val fileName = asset.fileName
    if (!fileName.startsWith(DATA_URI_PREFIX) || fileName.indexOf(BASE64_MARKER) <= 0) {
        logger.fail("Failed to predecode image `${asset.id}`: not an embedded base64 data URI.")
        return null
    }
    if (asset.width <= 0 || asset.height <= 0) {
        logger.fail("Skipped predecode for image `${asset.id}`: invalid dimensions ${asset.width}x${asset.height}.")
        return null
    }
    val data = try {
        Base64.decode(fileName.substring(fileName.indexOf(',') + 1), Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        logger.fail("data URL did not have correct base64 format.", e)
        return null
    }
    val opts = BitmapFactory.Options().apply {
        inScaled = true
        inDensity = 160
    }
    val bitmap = try {
        BitmapFactory.decodeByteArray(data, 0, data.size, opts)
    } catch (e: IllegalArgumentException) {
        logger.fail("Unable to decode image `${asset.id}`.", e)
        return null
    }
    if (bitmap == null) {
        logger.fail("Decoded image `${asset.id}` is null.")
        return null
    }
    return resizeBitmapIfNeeded(bitmap, asset.width, asset.height)
}

private fun resizeBitmapIfNeeded(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    if (bitmap.width == width && bitmap.height == height) {
        return bitmap
    }
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
    bitmap.recycle()
    return resizedBitmap
}
