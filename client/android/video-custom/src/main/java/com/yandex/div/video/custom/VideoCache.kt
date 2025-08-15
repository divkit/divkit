package com.yandex.div.video.custom

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.DefaultDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.ContentMetadata
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.internal.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "CustomVideoCache"

private const val DEFAULT_CACHE_SIZE = 100L * 1024 * 1024
private const val DEFAULT_VIDEO_CACHE_DIR = "video_custom_cache"

private const val VIDEO_DATABASE_NAME = "div_custom_video.db"
private const val VIDEO_DATABASE_VERSION = 1

class VideoCache @JvmOverloads constructor(
    private val imageLoader: DivImageLoader,
    private val imageCache: Provider<ImageCache>,
    context: Context,
    videoCacheDir: File = File(context.externalCacheDir, DEFAULT_VIDEO_CACHE_DIR),
    videoCacheSizeInBytes: Long = DEFAULT_CACHE_SIZE,
) {
    private val stubImageCache by lazy {
        imageCache.get()
    }

    private val videoCache by lazy {
        SimpleCache(
            videoCacheDir,
            LeastRecentlyUsedCacheEvictor(videoCacheSizeInBytes),
            getDatabaseProvider(context)
        )
    }

    private val cacheDataSourceFactory by lazy {
        CacheDataSource.Factory()
            .setCache(videoCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun getDatabaseProvider(context: Context) =
        DefaultDatabaseProvider(object : SQLiteOpenHelper(
            context,
            VIDEO_DATABASE_NAME,
            null,
            VIDEO_DATABASE_VERSION
        ) {
            override fun onCreate(db: SQLiteDatabase?) = Unit
            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) = Unit
        })

    private fun isVideoCached(dataSpec: DataSpec): Boolean {
        val key = dataSpec.key ?: return false
        val length = ContentMetadata.getContentLength(videoCache.getContentMetadata(key))
        if (length < 0) {
            return false
        }
        return videoCache.isCached(key, 0, length)
    }

    fun createMediaSource(mediaItem: MediaItem) =
        ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

    /**
     * @throws VideoCachingException.VideoCachingFailed when video caching is failed.
     */
    suspend fun cacheVideo(
        dataSpec: DataSpec,
    ): Unit = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            if (isVideoCached(dataSpec)) {
                continuation.resume(Unit)
                return@suspendCancellableCoroutine
            }

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                    if (requestLength == bytesCached) {
                        if (continuation.isActive) {
                            continuation.resume(Unit)
                        }
                    }
                }

            val cacheWriter = CacheWriter(
                cacheDataSourceFactory.createDataSource(),
                dataSpec,
                null,
                progressListener
            )

            try {
                cacheWriter.cache()
            } catch (e: IOException) {
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        VideoCachingException.VideoCachingFailed(dataSpec, e)
                    )
                }
            }
        }
    }

    suspend fun getStubImage(
        stubImageUrl: String
    ): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext stubImageCache.getImageBitmap(stubImageUrl) ?: try {
            cacheStubImage(stubImageUrl)
        } catch (e: VideoCachingException.ImageCachingFailed) {
            KLog.e(TAG, e) { "Failed caching stub image with url=$stubImageUrl" }
            null
        }
    }

    /**
     * @throws VideoCachingException.ImageCachingFailed when stub image caching is failed.
     * @return bitmap of image at [stubImageUrl] that was cached
     */
    suspend fun cacheStubImage(
        stubImageUrl: String
    ): Bitmap = withContext(Dispatchers.Main) {
        suspendCoroutine { continuation ->
            imageLoader.loadImage(stubImageUrl, object : DivImageDownloadCallback() {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    val image = cachedBitmap.bitmap
                    stubImageCache.storeImage(stubImageUrl, image)
                    continuation.resume(image)
                }

                override fun onError() {
                    continuation.resumeWithException(
                        VideoCachingException.ImageCachingFailed(stubImageUrl)
                    )
                }
            })
        }
    }
}

sealed class VideoCachingException(
    message: String?,
    cause: Throwable?,
) : Exception(message, cause) {

    class ImageCachingFailed(url: String, cause: Throwable? = null) :
        VideoCachingException("Failed caching image with url=$url", cause)

    class VideoCachingFailed(dataSpec: DataSpec, cause: Throwable? = null) :
        VideoCachingException("Failed caching video: $dataSpec", cause)
}
