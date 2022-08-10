package com.yandex.div.lottie

import org.json.JSONObject

internal sealed class LottieData {

    abstract val description: String

    data class Embedded(val json: JSONObject) : LottieData() {
        override val description = "Embedded Lottie JSON"
    }

    data class External(val url: String) : LottieData() {
        override val description = url
    }
}
