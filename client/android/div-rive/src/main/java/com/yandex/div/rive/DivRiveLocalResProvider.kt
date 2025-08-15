package com.yandex.div.rive

import androidx.annotation.RawRes

interface DivRiveLocalResProvider {

    @RawRes
    fun provideRes(url: String): Int?

    fun provideAssetFile(url: String): String?

    companion object {
        @JvmStatic
        val STUB = object : DivRiveLocalResProvider {
            override fun provideRes(url: String): Int? = null
            override fun provideAssetFile(url: String): String? = null
        }
    }
}
