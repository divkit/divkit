package com.yandex.div.core.preload

/**
 * Registry for reporting async preload completion. Used by extension handlers (e.g. lottie)
 * so that [DivPreloader] callback is notified when all preloads including extensions are done.
 */
interface PreloadingRegistry {
    /**
     * Register that new prelaad
     */
    fun registerPreloading(tag: String): PreloadingCompletion
}
