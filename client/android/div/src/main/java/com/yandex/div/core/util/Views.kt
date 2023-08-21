package com.yandex.div.core.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.doOnNextLayout

// https://issuetracker.google.com/issues/189446951#comment8
val View.isActuallyLaidOut: Boolean
    get() = width > 0 || height > 0

internal val View.isHierarchyLaidOut: Boolean
    get() = farthestLayoutCaller() == null

internal fun View.isLayoutRtl() =
    ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

inline fun View.doOnActualLayout(crossinline action: (view: View) -> Unit) {
    if (isActuallyLaidOut && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}

internal inline fun View.doOnHierarchyLayout(crossinline action: (view: View) -> Unit) {
    if (isHierarchyLaidOut && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextHierarchyLayout {
            action(it)
        }
    }
}

internal inline fun View.doOnNextHierarchyLayout(crossinline action: (view: View) -> Unit) {
    farthestLayoutCaller()?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}

private fun View.farthestLayoutCaller(): View? {
    var view: View? = this
    var caller: View? = null
    while (view != null) {
        if (!view.isActuallyLaidOut || view.isLayoutRequested) caller = view
        view = view.parent as? View
    }
    return caller
}

internal class SingleTimeOnAttachCallback(
    view: View,
    private var onAttachAction: (() -> Unit)?
) {
    init {
        if (view.isAttachedToWindow) {
            onAttach()
        }
    }

    fun onAttach() {
        onAttachAction?.invoke()
        onAttachAction = null
    }

    fun cancel() {
        onAttachAction = null
    }
}
