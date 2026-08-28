package com.yandex.div.video.m3

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.yandex.div.core.network.DivNetworkClient
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val VIDEO_CACHE_DIR = "divKit_video_cache"
private const val CACHE_SIZE = 90L * 1024 * 1024

@OptIn(UnstableApi::class)
public class ExoPlayerCache internal constructor(
    private val context: Context,
    internal val networkClient: DivNetworkClient?,
    internal val networkScope: CoroutineScope,
) {
    public constructor(context: Context) : this(context, null, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    internal constructor(context: Context, networkClient: DivNetworkClient?) :
        this(context, networkClient, CoroutineScope(SupervisorJob() + Dispatchers.IO))

    private val upstreamDataSourceFactory = networkClient
        ?.let { DefaultDataSource.Factory(context, DataSource.Factory { DivNetworkDataSource(it, networkScope) }) }
        ?: DefaultDataSource.Factory(context)

    internal val cacheDataSourceFactory: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
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
