package com.yandex.div.video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.internal.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DivExoPlayerCacheManager"

/**
 * This class will preload videos for ExoDivPlayer
 */
public class ExoPlayerVideoPreloader(
    private val context: Context,
    private val cache: ExoPlayerCache = ExoPlayerCache(context)
): DivPlayerPreloader {
    override fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference {
        if (src.isEmpty()) return DivPreloader.PreloadReference.EMPTY
        val job = GlobalScope.launch(Dispatchers.IO) { preCacheVideo(src) }
        return DivPreloader.PreloadReference {
            job.cancel()
        }
    }

    public fun createPlayerFactory(): DivPlayerFactory {
        return ExoDivPlayerFactory(context)
    }

    private fun preCacheVideo(src: List<Uri>) {
        src.forEach { videoUri ->
            val dataSpec = DataSpec(videoUri)

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                    val downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
                    KLog.d(TAG) { "downloadPercentage $downloadPercentage videoUri: $videoUri" }
                }

            cacheVideo(dataSpec, progressListener)
        }
    }

    private fun cacheVideo(
        dataSpec: DataSpec,
        progressListener: CacheWriter.ProgressListener
    ) {
        runCatching {
            CacheWriter(
                cache.cacheDataSourceFactory.createDataSource(),
                dataSpec,
                null,
                progressListener
            ).cache()
        }.onFailure {
            KLog.e(TAG) { "error on loading video with URL = \"${dataSpec.uri}\"" }
            it.printStackTrace()
        }
    }
}
