package com.yandex.div.video.m3

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C.CONTENT_TYPE_DASH
import androidx.media3.common.C.CONTENT_TYPE_HLS
import androidx.media3.common.C.CONTENT_TYPE_OTHER
import androidx.media3.common.C.CONTENT_TYPE_RTSP
import androidx.media3.common.C.CONTENT_TYPE_SS
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.yandex.div.internal.KAssert

@OptIn(UnstableApi::class)
internal class ExoDivMediaSourceAbstractFactory(
    context: Context,
    cacheDataSourceFactory: CacheDataSource.Factory?
) {
    private val dataSourceFactory by lazy {
        cacheDataSourceFactory ?: DefaultDataSource.Factory(context)
    }

    fun create(fileName: String): MediaSource.Factory? {
        val uri = Uri.parse(fileName)
        return when (val contentType = Util.inferContentType(uri)) {
            CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
            CONTENT_TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
            CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
            CONTENT_TYPE_RTSP -> RtspMediaSource.Factory()
            CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
            else -> {
                KAssert.fail { "Unsupported content type: $contentType of file $fileName" }
                null
            }
        }
    }
}
