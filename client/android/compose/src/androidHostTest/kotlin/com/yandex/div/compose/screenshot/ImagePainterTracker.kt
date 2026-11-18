package com.yandex.div.compose.screenshot

import androidx.compose.ui.test.IdlingResource
import coil3.compose.AsyncImagePainter.State
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
class ImagePainterTracker : IdlingResource {
    private val activeRequests = AtomicInt(0)

    override val isIdleNow: Boolean
        get() = activeRequests.load() == 0

    fun onStateChanged(state: State) {
        when (state) {
            State.Empty -> Unit
            is State.Loading -> activeRequests.incrementAndFetch()
            is State.Success, is State.Error -> {
                // Empty → Success transition is possible
                if (activeRequests.load() > 0) {
                    activeRequests.decrementAndFetch()
                }
            }
        }
    }
}
