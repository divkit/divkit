package com.yandex.div.video.custom

interface VideoCustomActionListener {

    /**
     * Called when video becomes visible to the user for the first time
     */
    fun onVideoStartedShowing(videoId: String) = Unit

    /**
     * Called when error related to playback occurs
     */
    fun onPlaybackError(videoId: String, cause: Exception) = Unit

    /**
     * When called, it indicates that first [showDurationMs] were displayed by player
     */
    fun onPlaybackFinished(videoId: String, showDurationMs: Long) = Unit
}
