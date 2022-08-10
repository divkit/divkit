package com.yandex.div.video.custom

import com.google.android.exoplayer2.upstream.DataSpec
import com.yandex.div.json.getStringOrNull
import com.yandex.div2.DivCustom

private const val VIDEO_URL_KEY = "url"
private const val REPEAT_KEY = "repeat"
private const val STUB_IMAGE_URL_KEY = "stub_image_url"
private const val START_MODE_KEY = "start_mode"

private const val START_MODE_WHEN_READY = "when_ready"
private const val START_MODE_BY_DEEPLINK = "by_deeplink"

internal data class VideoConfig(
    val id: String?,
    val url: String,
    val stubImageUrl: String?,
    val repeat: Boolean,
    val startMode: VideoStartMode
)

internal enum class VideoStartMode {
    WHEN_READY,
    BY_DEEPLINK
}

internal val DivCustom.videoConfig: VideoConfig
    get() = VideoConfig(
        id = id,
        url = customProps?.getStringOrNull(VIDEO_URL_KEY) ?: "",
        stubImageUrl = customProps?.getStringOrNull(STUB_IMAGE_URL_KEY),
        repeat = customProps?.optBoolean(REPEAT_KEY, false) ?: false,
        startMode = customProps?.getStringOrNull(START_MODE_KEY).resolveStartMode()
    )

internal val VideoConfig.dataSpec: DataSpec
    get() = DataSpec.Builder()
        .setUri(url)
        .setKey(url)
        .build()

private fun String?.resolveStartMode(): VideoStartMode = when (this) {
    START_MODE_WHEN_READY -> VideoStartMode.WHEN_READY
    START_MODE_BY_DEEPLINK -> VideoStartMode.BY_DEEPLINK
    else -> VideoStartMode.WHEN_READY
}

internal fun isValid(config: VideoConfig): Boolean {
    val requiredId = config.startMode == VideoStartMode.BY_DEEPLINK
    val hasIdIfRequired = !requiredId || config.id != null

    return config.url.isNotEmpty() && hasIdIfRequired
}
