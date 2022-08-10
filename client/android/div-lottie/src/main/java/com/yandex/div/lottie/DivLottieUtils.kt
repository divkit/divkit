package com.yandex.div.lottie

import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal fun View.launchOnAttachedToWindow(
    action: suspend CoroutineScope.() -> Unit
) {
    val listener = ScopedOnAttachStateChangeListener(action)
    if (ViewCompat.isAttachedToWindow(this)) {
        listener.performActionInScope()
    }
    addOnAttachStateChangeListener(listener)
    setTag(R.id.lottie_on_attach_to_window_listener, listener)
}

internal fun View.clearOnAttachedToWindowScope() {
    val listener = getTag(R.id.lottie_on_attach_to_window_listener) as? ScopedOnAttachStateChangeListener
    if (listener != null) {
        listener.clearScope()
        removeOnAttachStateChangeListener(listener)
    }
}

private class ScopedOnAttachStateChangeListener(
    val action: suspend CoroutineScope.() -> Unit
) : View.OnAttachStateChangeListener {

    private var attachScope: CoroutineScope? = null

    override fun onViewAttachedToWindow(view: View) {
        performActionInScope()
    }

    override fun onViewDetachedFromWindow(view: View) {
        clearScope()
    }

    fun performActionInScope() {
        val scope = attachScope ?: CoroutineScope(Dispatchers.Unconfined)
        scope.launch {
            action()
        }
        attachScope = scope
    }

    fun clearScope() {
        attachScope?.cancel()
        attachScope = null
    }
}
