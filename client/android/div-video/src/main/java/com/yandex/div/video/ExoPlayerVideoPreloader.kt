package com.yandex.div.video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.internal.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DivExoPlayerCacheManager"

/**
 * This class will preload videos for ExoDivPlayer
 */
@Deprecated("Use com.yandex.div.video.m3.ExoPlayerVideoPreloader")
public class ExoPlayerVideoPreloader(
    private val context: Context,
    private val cache: ExoPlayerCache = ExoPlayerCache(context)
): DivPlayerPreloader {
    override fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference {
        return preloadVideoInternal(src) {}
    }

    override fun preloadVideo(src: List<Uri>, callback: (List<PreloadResult>) -> Unit): DivPreloader.PreloadReference {
        return preloadVideoInternal(src, callback)
    }
    private fun preloadVideoInternal(src: List<Uri>, callback: (List<PreloadResult>) -> Unit): DivPreloader.PreloadReference {
        if (src.isEmpty()) return DivPreloader.PreloadReference.EMPTY
        val job = GlobalScope.launch(Dispatchers.IO) {
            val results = mutableListOf<PreloadResult>()
            preCacheVideo(src) { uri, error ->
                results.add(UriPreloadResult(uri, error))
            }
            callback(results)
        }
        return DivPreloader.PreloadReference {
            job.cancel()
        }
    }

    public fun createPlayerFactory(): DivPlayerFactory {
        return ExoDivPlayerFactory(context)
    }

    private fun preCacheVideo(src: List<Uri>, callback: (Uri, Throwable?) -> Unit) {
        src.forEach { videoUri ->
            val dataSpec = DataSpec(videoUri)

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                    val downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
                    KLog.d(TAG) { "downloadPercentage $downloadPercentage videoUri: $videoUri" }
                }

            val error = cacheVideo(dataSpec, progressListener)
            callback(videoUri, error)
        }
    }

    private fun cacheVideo(
        dataSpec: DataSpec,
        progressListener: CacheWriter.ProgressListener
    ): Throwable? {
        return runCatching {
            CacheWriter(
                cache.cacheDataSourceFactory.createDataSource(),
                dataSpec,
                null,
                progressListener
            ).cache()
        }.onFailure {
            KLog.e(TAG) { "error on loading video with URL = \"${dataSpec.uri}\"" }
            it.printStackTrace()
        }.exceptionOrNull()
    }
}
