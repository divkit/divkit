package com.yandex.div.video

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.yandex.div.internal.KAssert

class ExoDivMediaSourceAbstractFactory(
    context: Context
) {
    private val mediaDataSourceFactory by lazy {
        DefaultDataSourceFactory(context)
    }

    fun create(fileName: String): MediaSourceFactory? {
        return when (val contentType = Util.inferContentType(fileName)) {
            C.TYPE_DASH -> DashMediaSource.Factory(mediaDataSourceFactory)
            C.TYPE_SS -> SsMediaSource.Factory(mediaDataSourceFactory)
            C.TYPE_HLS -> HlsMediaSource.Factory(mediaDataSourceFactory)
            C.TYPE_RTSP -> RtspMediaSource.Factory()
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            else -> {
                KAssert.fail { "Unsupported content type: $contentType of file $fileName" }
                null
            }
        }
    }
}
