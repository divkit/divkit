package com.yandex.div.video.m3

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

private const val VIDEO_CACHE_DIR = "divKit_video_cache"
private const val CACHE_SIZE = 90L * 1024 * 1024

@OptIn(UnstableApi::class)
public class ExoPlayerCache(private val context: Context) {
    internal val cacheDataSourceFactory: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
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
