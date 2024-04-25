package com.yandex.div.video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.internal.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

private const val VIDEO_CACHE_DIR = "divKit_video_cache"
private const val TAG = "DivExoPlayerCacheManager"
private const val CACHE_SIZE = 90L * 1024 * 1024

/**
 * This class will preload videos for ExoDivPlayer
 */
public class ExoPlayerVideoPreloader(private val context: Context): DivPlayerPreloader {
    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
    private var cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    override fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference {
        if (src.isEmpty()) return DivPreloader.PreloadReference.EMPTY
        val job = GlobalScope.launch(Dispatchers.IO) { preCacheVideo(src) }
        return DivPreloader.PreloadReference {
            job.cancel()
        }
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
                cacheDataSourceFactory.createDataSource(),
                dataSpec,
                null,
                progressListener
            ).cache()
        }.onFailure {
            KLog.e(TAG) { "error on loading video with URL = \"${dataSpec.uri}\"" }
            it.printStackTrace()
        }
    }

    private val simpleCache: SimpleCache get() {
        synchronized(lock) {
            return if (_simpleCache == null) {
                val cacheDir = File(context.externalCacheDir, VIDEO_CACHE_DIR)
                val cacheEvictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
                val exoDatabaseProvider = StandaloneDatabaseProvider(context)
                SimpleCache(cacheDir, cacheEvictor, exoDatabaseProvider).also {
                    _simpleCache = it
                }
            } else {
                _simpleCache!!
            }
        }
    }

    private companion object {
        private var _simpleCache: SimpleCache? = null
        private val lock = Object()
    }
}
