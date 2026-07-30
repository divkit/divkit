@file:JvmName("OnPreDrawListeners")

package com.yandex.div.internal.view

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import com.yandex.div.core.annotations.InternalApi

@InternalApi
fun onPreDrawListener(
    action: () -> Unit
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = @SuppressLint("OnPreDrawListenerIssue") {
            action()
            true
        },
        overrideStrategy = DrawingPassOverrideStrategy.AlwaysPass
    )
}

@InternalApi
fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy,
    action: () -> Boolean
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = action,
        overrideStrategy
    )
}

@SuppressLint("OnPreDrawListenerIssue")
private class OverridableOnPreDrawListener @JvmOverloads constructor(
    private val delegate: ViewTreeObserver.OnPreDrawListener,
    private val overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe
) : ViewTreeObserver.OnPreDrawListener {

    override fun onPreDraw(): Boolean {
        val proceed = delegate.onPreDraw()
        return overrideStrategy.overrideDrawingPass(delegate, proceed)
    }
}
