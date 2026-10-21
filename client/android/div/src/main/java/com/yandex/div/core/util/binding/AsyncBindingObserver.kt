package com.yandex.div.core.util.binding

import androidx.annotation.MainThread
import com.yandex.div.core.DivBindingCallback
import com.yandex.div.core.DivBindingPhase

/** Receives binding phases on main and adapts the original Boolean completion callback. */
internal fun interface AsyncBindingObserver {
    @MainThread
    fun onBindingPhase(phase: DivBindingPhase)

    /** Forwards the original `onComplete` result; `false` does not necessarily mean a binding failure. */
    fun onLegacyComplete(result: Boolean) = Unit
}

internal fun ((Boolean) -> Unit)?.asBindingObserver(): AsyncBindingObserver? = this?.let(::OnCompleteAdapter)

internal fun DivBindingCallback.asBindingObserver(): AsyncBindingObserver = AsyncBindingObserver(this::onBindingPhase)

private class OnCompleteAdapter(private val onComplete: (Boolean) -> Unit) : AsyncBindingObserver {
    override fun onLegacyComplete(result: Boolean) = onComplete(result)

    @MainThread
    override fun onBindingPhase(phase: DivBindingPhase) = Unit
}
