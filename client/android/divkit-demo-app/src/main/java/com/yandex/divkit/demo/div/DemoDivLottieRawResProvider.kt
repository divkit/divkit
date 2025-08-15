package com.yandex.divkit.demo.div

import com.yandex.div.lottie.DivLottieRawResProvider
import com.yandex.divkit.demo.R

object DemoDivLottieRawResProvider : DivLottieRawResProvider {
    override fun provideRes(url: String): Int? {
        return when (url) {
            "res://love" -> R.raw.love_anim
            else -> null
        }
    }

    override fun provideAssetFile(url: String): String? {
        return when (url) {
            "asset://banana" -> "lottie/banana.json"
            "asset://spinner" -> "lottie/spinner.json"
            else -> null
        }
    }

    override fun provideAssetFolder(): String = "lottie/"
}
