package com.yandex.div.core.preload

import com.yandex.div.internal.Assert

internal class PreloadingCompletionImpl(
    private val id: String,
    private val onComplete: () -> Unit,
) : PreloadingCompletion {
    var isFailed: Boolean = false
        private set

    var isCompleted = false
        private set

    var result: PreloadResult? = null
        private set

    override fun onCompleted(preloadResult: PreloadResult) {
        if (isCompleted) {
            Assert.fail("Preloading '$id' is already completed!")
            return
        }
        isCompleted = true
        result = preloadResult
        isFailed = result?.filterErrorResults()?.firstOrNull() != null
        onComplete()
    }
}
