package com.yandex.div.core.util.binding

import com.yandex.div.internal.util.UiThreadHandler
import java.util.concurrent.atomic.AtomicBoolean

/** Opt-in requests have one terminal notification, including cleanup before publication. */
internal class BindingRequestCancellation(
    private val onCancelled: () -> Unit,
    private val release: (BindingRequestCancellation) -> Unit,
) {
    private val pending = AtomicBoolean(true)

    fun complete(callback: () -> Unit) {
        if (!pending.compareAndSet(true, false)) return
        release(this)
        callback()
    }

    fun cancel() = complete { UiThreadHandler.postOnMainThread(onCancelled) }
}
