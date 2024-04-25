package com.yandex.div.video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C.CONTENT_TYPE_DASH
import com.google.android.exoplayer2.C.CONTENT_TYPE_HLS
import com.google.android.exoplayer2.C.CONTENT_TYPE_OTHER
import com.google.android.exoplayer2.C.CONTENT_TYPE_RTSP
import com.google.android.exoplayer2.C.CONTENT_TYPE_SS
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util
import com.yandex.div.internal.KAssert

internal class ExoDivMediaSourceAbstractFactory(
    context: Context
) {
    private val mediaDataSourceFactory by lazy {
        DefaultDataSource.Factory(context)
    }

    fun create(fileName: String): MediaSource.Factory? {
        val uri = Uri.parse(fileName)
        return when (val contentType = Util.inferContentType(uri)) {
            CONTENT_TYPE_DASH -> DashMediaSource.Factory(mediaDataSourceFactory)
            CONTENT_TYPE_SS -> SsMediaSource.Factory(mediaDataSourceFactory)
            CONTENT_TYPE_HLS -> HlsMediaSource.Factory(mediaDataSourceFactory)
            CONTENT_TYPE_RTSP -> RtspMediaSource.Factory()
            CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            else -> {
                KAssert.fail { "Unsupported content type: $contentType of file $fileName" }
                null
            }
        }
    }
}
