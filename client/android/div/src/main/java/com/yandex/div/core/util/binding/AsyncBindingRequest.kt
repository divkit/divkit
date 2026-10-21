package com.yandex.div.core.util.binding

import androidx.annotation.MainThread
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.internal.util.UiThreadHandler

/** Delivers lifecycle events once without controlling binding or recovery. */
internal class AsyncBindingRequest(
    private val observer: AsyncBindingObserver?,
    private val reportError: (Throwable) -> Unit,
    private val tag: DivDataTag,
    private val generation: Int,
) {
    private var lastPhase: DivBindingPhase? = null

    val isPending: Boolean
        @Synchronized get() = lastPhase == null || lastPhase == DivBindingPhase.PREPARED

    val isPrepared: Boolean
        @Synchronized get() = lastPhase == DivBindingPhase.PREPARED

    fun report(phase: DivBindingPhase, failureReason: String? = null) = UiThreadHandler.executeOnMainThread {
        val changed = synchronized(this) {
            if (!isPending || lastPhase == phase) return@synchronized false
            lastPhase = phase
            true
        }
        if (!changed) return@executeOnMainThread
        if (failureReason != null) {
            runCatching {
                reportError(IllegalStateException(
                    "Async binding failed: request=${Integer.toHexString(System.identityHashCode(this))}, " +
                        "tag=${tag.id}, generation=$generation, reason=$failureReason"
                ))
            }
        }
        notifyObserver(phase)
    }

    fun cancel() {
        synchronized(this) {
            if (!isPending) return
            lastPhase = DivBindingPhase.CANCELLED
        }
        UiThreadHandler.postOnMainThread { notifyObserver(DivBindingPhase.CANCELLED) }
    }

    @MainThread
    private fun notifyObserver(phase: DivBindingPhase) {
        val observer = observer ?: return
        runCatching { observer.onBindingPhase(phase) }.onFailure { error ->
            runCatching { reportError(error) }
        }
    }
}
