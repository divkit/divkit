package com.yandex.div.rive

import android.content.Context
import android.net.Uri
import android.util.LruCache
import androidx.annotation.WorkerThread

internal class DivRiveRepository(
    maxCacheSize: Int,
    private val context: Context,
    private val networkDelegate: DivRiveNetworkDelegate,
    private val localResProvider: DivRiveLocalResProvider
) {
    private val cache: LruCache<String, ByteArray> = object : LruCache<String, ByteArray>(maxCacheSize) {
        override fun sizeOf(key: String, value: ByteArray): Int {
            return value.size
        }
    }

    @Synchronized
    private fun getCached(key: String): ByteArray? = cache[key]

    @Synchronized
    private fun putIntoCache(key: String, array: ByteArray) {
        if (array.size > cache.maxSize()) {
            cache.remove(key)
        } else {
            cache.put(key, array)
        }
    }

    @WorkerThread
    fun receiveRiveAnimation(url: String): RiveResult {
        return try {
            when (Uri.parse(url).scheme) {
                HTTP_SCHEME, HTTPS_SCHEME -> {
                    val cached = getCached(key = url)
                    val responseBytes: ByteArray
                    if (cached == null) {
                        responseBytes = networkDelegate.load(url)
                        putIntoCache(key = url, array = responseBytes)
                    } else {
                        responseBytes = cached
                    }
                    RiveResult(responseBytes)
                }
                RES_SCHEME -> {
                    val rawRes = localResProvider.provideRes(url)
                    if (rawRes == null) {
                        RiveResult(IllegalArgumentException("Failed to map $url to internal resource"))
                    } else {
                        RiveResult(context.resources.openRawResource(rawRes).readBytes())
                    }
                }
                ASSET_SCHEME -> {
                    val assetFileAddress = localResProvider.provideAssetFile(url)
                    if (assetFileAddress == null) {
                        RiveResult(IllegalArgumentException("Failed to map $url to internal resource"))
                    } else {
                        RiveResult(context.assets.open(assetFileAddress).readBytes())
                    }
                }
                else -> RiveResult(IllegalArgumentException("Failed to retrieve rive animation from $url"))
            }
        } catch (throwable: Throwable) {
            RiveResult(throwable)
        }
    }

    class RiveResult private constructor(
        val byteArray: ByteArray? = null,
        val throwable: Throwable? = null
    ) {
        constructor(byteArray: ByteArray) : this(byteArray, null)
        constructor(throwable: Throwable) : this(null, throwable)
    }

    private companion object {
        const val HTTP_SCHEME = "http"
        const val HTTPS_SCHEME = "https"
        const val RES_SCHEME = "res"
        const val ASSET_SCHEME = "asset"
    }
}
