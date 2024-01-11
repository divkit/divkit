package com.yandex.div.core.player

import android.net.Uri
import com.yandex.div.core.DivPreloader

/**
 * Describes the interface for preloading video for your player.
 */
interface DivPlayerPreloader {
    fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference

    companion object {
        @JvmField
        val STUB: DivPlayerPreloader = object : DivPlayerPreloader {
            override fun preloadVideo(src: List<Uri>) = DivPreloader.PreloadReference.EMPTY
        }
    }
}
