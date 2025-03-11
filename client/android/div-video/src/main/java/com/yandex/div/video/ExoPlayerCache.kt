package com.yandex.div.video

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

private const val VIDEO_CACHE_DIR = "divKit_video_cache"
private const val CACHE_SIZE = 90L * 1024 * 1024

public class ExoPlayerCache(private val context: Context) {
    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)

    internal val cacheDataSourceFactory: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private val simpleCache: SimpleCache
        get() {
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
