package com.yandex.div.internal.extensions.lottie

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.Expression

@InternalApi
data class LottieExtensionParams(
    val data: LottieData,
    val isPlaying: Expression<Boolean>?,
    val repeatCount: Int,
    val repeatMode: LottieRepeatMode,
    val repeats: List<LottieRepeat>
)

@InternalApi
sealed class LottieData {
    abstract val description: String

    data class Asset(val assetName: String) : LottieData() {
        override val description = assetName
    }

    data class Json(val json: String) : LottieData() {
        override val description = "Embedded Lottie JSON"
    }

    data class RawRes(
        @get:androidx.annotation.RawRes
        @param:androidx.annotation.RawRes
        val id: Int,
        val url: String
    ) : LottieData() {
        override val description = url
    }

    data class Url(val url: String) : LottieData() {
        override val description = url
    }
}

@InternalApi
data class LottieRepeat(
    val count: Int,
    val mode: LottieRepeatMode,
    val minFrame: Int?,
    val maxFrame: Int?
)

@InternalApi
enum class LottieRepeatMode {
    RESTART,
    REVERSE
}
