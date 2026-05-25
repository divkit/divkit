package com.yandex.div.test.data

import android.net.Uri
import androidx.core.net.toUri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import com.yandex.div2.DivVideoSource
import org.json.JSONObject

fun video(
    autostart: Expression<Boolean> = constant(false),
    bufferingActions: List<DivAction>? = null,
    elapsedTimeVariable: String? = null,
    endActions: List<DivAction>? = null,
    fatalActions: List<DivAction>? = null,
    id: String? = null,
    muted: Expression<Boolean> = constant(false),
    pauseActions: List<DivAction>? = null,
    playbackSpeed: Expression<Double> = constant(1.0),
    playerSettingsPayload: Expression<JSONObject>? = null,
    preview: Expression<String>? = null,
    repeatable: Expression<Boolean> = constant(false),
    resumeActions: List<DivAction>? = null,
    scale: Expression<DivVideoScale> = constant(DivVideoScale.FIT),
    videoSources: List<DivVideoSource>? = null,
): Div {
    return Div.Video(
        value = DivVideo(
            autostart = autostart,
            bufferingActions = bufferingActions,
            elapsedTimeVariable = elapsedTimeVariable,
            endActions = endActions,
            fatalActions = fatalActions,
            id = id,
            muted = muted,
            pauseActions = pauseActions,
            playbackSpeed = playbackSpeed,
            playerSettingsPayload = playerSettingsPayload,
            preview = preview,
            repeatable = repeatable,
            resumeActions = resumeActions,
            scale = scale,
            videoSources = videoSources,
        )
    )
}

fun videoSource(
    url: Expression<Uri> = constant("empty://".toUri()),
    mimeType: Expression<String> = constant("video/mp4"),
    bitrate: Expression<Long>? = null,
    resolution: DivVideoSource.Resolution? = null,
): DivVideoSource {
    return DivVideoSource(
        bitrate = bitrate,
        mimeType = mimeType,
        resolution = resolution,
        url = url,
    )
}
