package com.yandex.div.video.custom

internal class VideoCustomActionNotifier {
    private val listeners = mutableListOf<VideoCustomActionListener>()

    fun notifyVideoStartedShowing(
        videoId: String
    ) = listeners.forEach { listener ->
        listener.onVideoStartedShowing(videoId)
    }

    fun notifyPlaybackError(
        videoId: String,
        cause: Exception
    ) = listeners.forEach { listener ->
        listener.onPlaybackError(videoId, cause)
    }

    fun notifyPlaybackFinished(
        videoId: String,
        positionMs: Long
    ) = listeners.forEach { listener ->
        listener.onPlaybackFinished(videoId, positionMs)
    }

    fun addListener(listener: VideoCustomActionListener) {
        listeners += listener
    }

    fun removeListener(listener: VideoCustomActionListener) {
        listeners -= listener
    }
}
