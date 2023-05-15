package com.yandex.div.core.player

/**
 * Describes the player interface compatible with div-video.
 */
interface DivPlayer {
    interface Observer {
        /**
         * Called when playback resumes.
         */
        fun onPlay() = Unit

        /**
         * Called when playback has been paused by an action.
         */
        fun onPause() = Unit

        /**
         * Called when playback has been paused due to the end of the buffer.
         */
        fun onBuffering() = Unit

        /**
         * Called at the end of playback. Never called if video source is repeatable.
         */
        fun onEnd() = Unit

        /**
         * Called when engine is unable to play due to internal error.
         */
        fun onFatal() = Unit

        /**
         * Called every second, passes the current playback time.
         */
        fun onCurrentTimeChange(timeMs: Long) = Unit
    }

    fun addObserver(observer: Observer) = Unit

    fun removeObserver(observer: Observer) = Unit

    fun setSource(sourceVariants: List<DivVideoSource>, config: DivPlayerPlaybackConfig) = Unit

    fun play() = Unit

    fun pause() = Unit

    fun seek(toMs: Long) = Unit

    fun release() = Unit
}
