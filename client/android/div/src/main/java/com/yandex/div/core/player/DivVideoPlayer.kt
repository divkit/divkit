package com.yandex.div.core.player

/**
 * Describes the player interface compatible with div-video.
 */
interface DivVideoPlayer {
    interface Observer {
        /**
         * Called when playback resumes.
         */
        fun onResume()

        /**
         * Called when playback was interrupted.
         */
        fun onPause(reason: DivVideoPauseReason)

        /**
         * Called every second, passes the current playback time.
         */
        fun onCurrentTimeUpdate(timeMs: Long)
    }

    fun addObserver(observer: Observer) = Unit

    fun removeObserver(observer: Observer) = Unit

    fun play() = Unit

    fun pause() = Unit

    fun seek(toMs: Long) = Unit
}
