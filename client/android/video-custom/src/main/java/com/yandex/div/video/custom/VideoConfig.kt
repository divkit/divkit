package com.yandex.div.video.custom

import com.google.android.exoplayer2.upstream.DataSpec
import com.yandex.div.internal.util.getStringOrNull
import com.yandex.div2.DivCustom

private const val VIDEO_URL_KEY = "url"
private const val REPEAT_KEY = "repeat"
private const val STUB_IMAGE_URL_KEY = "stub_image_url"
private const val START_MODE_KEY = "start_mode"
private const val Z_ORDER_MODE_KEY = "z_order_mode"

private const val START_MODE_WHEN_READY = "when_ready"
private const val START_MODE_BY_DEEPLINK = "by_deeplink"

private const val Z_ORDER_MODE_ON_TOP = "on_top"
private const val Z_ORDER_MODE_MEDIA_OVERLAY = "media_overlay"

internal data class VideoConfig(
    val id: String?,
    val url: String,
    val stubImageUrl: String?,
    val repeat: Boolean,
    val startMode: VideoStartMode,
    val zOrderMode: ZOrderMode,
)

internal enum class VideoStartMode {
    WHEN_READY,
    BY_DEEPLINK
}

internal enum class ZOrderMode {
    ON_TOP,
    MEDIA_OVERLAY
}

internal val DivCustom.videoConfig: VideoConfig
    get() = VideoConfig(
        id = id,
        url = customProps?.getStringOrNull(VIDEO_URL_KEY) ?: "",
        stubImageUrl = customProps?.getStringOrNull(STUB_IMAGE_URL_KEY),
        repeat = customProps?.optBoolean(REPEAT_KEY, false) ?: false,
        startMode = customProps?.getStringOrNull(START_MODE_KEY).resolveStartMode(),
        zOrderMode = customProps?.getStringOrNull(Z_ORDER_MODE_KEY).resolveZOrderMode(),
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

private fun String?.resolveZOrderMode(): ZOrderMode = when (this) {
    Z_ORDER_MODE_ON_TOP -> ZOrderMode.ON_TOP
    Z_ORDER_MODE_MEDIA_OVERLAY -> ZOrderMode.MEDIA_OVERLAY
    else -> ZOrderMode.ON_TOP
}

internal fun isValid(config: VideoConfig): Boolean {
    val requiredId = config.startMode == VideoStartMode.BY_DEEPLINK
    val hasIdIfRequired = !requiredId || config.id != null

    return config.url.isNotEmpty() && hasIdIfRequired
}
