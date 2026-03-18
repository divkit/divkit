package com.yandex.div.core.preload

/**
 * Callback for reporting completion of an async preload
 * registered via [PreloadingRegistry.registerPreloading].
 */
interface PreloadingCompletion {
    /**
     * Reports that the preload has finished.
     */
    fun onCompleted(preloadResult: PreloadResult)
}
