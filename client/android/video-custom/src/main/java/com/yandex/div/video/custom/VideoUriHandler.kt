package com.yandex.div.video.custom

import android.net.Uri
import com.yandex.div.core.uri.UriHandler

private const val SCHEME_VIDEO_CUSTOM = "custom_video"

private const val AUTHORITY_PLAY = "play"
private const val AUTHORITY_PAUSE = "pause"
private const val AUTHORITY_RESET = "reset"

private const val PARAM_VIDEO_ID = "id"

@Deprecated("Use div.video.m3 package")
class VideoCustomUriHandler(
    private val videoCustomViewController: VideoCustomViewController
) : UriHandler {
    override fun handleUri(uri: Uri): Boolean {
        if (uri.scheme != SCHEME_VIDEO_CUSTOM) return false
        val id = uri.getQueryParameter(PARAM_VIDEO_ID) ?: return false

        return when(uri.authority) {
            AUTHORITY_PLAY -> {
                videoCustomViewController.playVideo(id)
                true
            }
            AUTHORITY_PAUSE -> {
                videoCustomViewController.pauseVideo(id)
                true
            }
            AUTHORITY_RESET -> {
                videoCustomViewController.resetVideo(id)
                true
            }
            else -> false
        }
    }
}
