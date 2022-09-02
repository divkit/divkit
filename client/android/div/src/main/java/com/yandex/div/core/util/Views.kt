package com.yandex.div.core.util

import android.view.View

internal val View.isHierarchyLaidOut: Boolean
    get() = farthestLayoutCaller() == null

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
        if (!view.isLaidOut) caller = view
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
