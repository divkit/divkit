package com.yandex.div.core.util

import android.os.Build
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

internal fun View.getIndices(start: Int, count: Int) =
    if (isLayoutRtl()) start + count - 1 downTo start else start until start + count

inline fun View.doOnActualLayout(crossinline action: (view: View) -> Unit) {
    if (isActuallyLaidOut && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}

internal fun extractMaxHeight(heightMeasureSpec: Int): Int {
    return if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST) {
        View.MeasureSpec.getSize(heightMeasureSpec)
    } else {
        Int.MAX_VALUE
    }
}

internal fun View.makeFocusable() {
    isFocusable = true
    isFocusableInTouchMode = true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        defaultFocusHighlightEnabled = false
    }
}

internal inline fun View.doOnHierarchyLayout(
    crossinline action: (view: View) -> Unit,
    onEnqueuedAction: () -> Unit
) {
    if (isHierarchyLaidOut && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextHierarchyLayout(action, onEnqueuedAction)
    }
}

internal inline fun View.doOnNextHierarchyLayout(
    crossinline action: (view: View) -> Unit,
    onEnqueuedAction: () -> Unit
) {
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
    }).also {
        onEnqueuedAction()
    }
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
