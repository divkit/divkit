package com.yandex.div.video.m3

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheWriter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.internal.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "DivExoPlayerCacheManager"

/**
 * This class will preload videos for ExoDivPlayer
 */
@OptIn(UnstableApi::class)
public class ExoPlayerVideoPreloader(
    private val context: Context,
    private val cache: ExoPlayerCache = ExoPlayerCache(context)
): DivPlayerPreloader {
    public constructor(context: Context, networkClient: DivNetworkClient) :
        this(context, ExoPlayerCache(context, networkClient))

    public constructor(
        context: Context,
        networkClient: DivNetworkClient,
        networkScope: CoroutineScope,
    ) : this(context, ExoPlayerCache(context, networkClient, networkScope))

    override fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference {
        return preloadVideoInternal(src) {}
    }

    override fun preloadVideo(src: List<Uri>, callback: (List<PreloadResult>) -> Unit): DivPreloader.PreloadReference {
        return preloadVideoInternal(src, callback)
    }

    private fun preloadVideoInternal(
        src: List<Uri>,
        callback: (List<PreloadResult>) -> Unit,
    ): DivPreloader.PreloadReference {
        if (src.isEmpty()) return DivPreloader.PreloadReference.EMPTY
        val job = cache.networkScope.launch { preCacheVideo(src, callback) }
        return DivPreloader.PreloadReference {
            job.cancel()
        }
    }

    public fun createPlayerFactory(): DivPlayerFactory {
        val networkClient = cache.networkClient
        return if (networkClient == null) {
            ExoDivPlayerFactory(context)
        } else {
            ExoDivPlayerFactory(context, networkClient, cache.networkScope)
        }
    }

    private fun preCacheVideo(src: List<Uri>, callback: (List<PreloadResult>) -> Unit) {
        val results = mutableListOf<PreloadResult>()

        src.forEach { videoUri ->
            val dataSpec = DataSpec(videoUri)

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                    val downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
                    KLog.d(TAG) { "downloadPercentage $downloadPercentage videoUri: $videoUri" }
                }

            val error = cacheVideo(dataSpec, progressListener)
            results.add(UriPreloadResult(videoUri, error))
        }
        callback(results)
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
