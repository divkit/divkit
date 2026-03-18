package com.yandex.div.core.player

import android.net.Uri
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult

/**
 * Describes the interface for preloading video for your player.
 */
interface DivPlayerPreloader {
    fun preloadVideo(src: List<Uri>): DivPreloader.PreloadReference

    fun preloadVideo(
        src: List<Uri>,
        callback: (List<PreloadResult>) -> Unit,
    ): DivPreloader.PreloadReference {
        val ref = preloadVideo(src)
        val error = NotImplementedError("Please implement DivPlayerPreloader.preloadVideo(src, callback)!")
        callback(
            src.map { uri -> UriPreloadResult(uri, error) }
        )
        return ref
    }

    companion object {
        @JvmField
        val STUB: DivPlayerPreloader = object : DivPlayerPreloader {
            override fun preloadVideo(src: List<Uri>) = DivPreloader.PreloadReference.EMPTY
        }
    }
}
