package com.yandex.div.core.player

/**
 * Describes the player interface compatible with div-video.
 */
interface DivPlayer {
    interface Observer {
        /**
         * Called when playback resumes.
         */
        fun onResume() = Unit

        /**
         * Called when playback was interrupted.
         */
        fun onPause(reason: DivVideoPauseReason) = Unit

        /**
         * Called every second, passes the current playback time.
         */
        fun onCurrentTimeUpdate(timeMs: Long) = Unit
    }

    fun addObserver(observer: Observer) = Unit

    fun removeObserver(observer: Observer) = Unit

    fun setSource(sourceVariants: List<DivVideoSource>, config: DivPlayerPlaybackConfig) = Unit

    fun play() = Unit

    fun pause() = Unit

    fun seek(toMs: Long) = Unit

    fun release() = Unit
}
