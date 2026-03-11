@file:JvmName("OnPreDrawListeners")

package com.yandex.div.internal.view

import android.annotation.SuppressLint
import android.view.ViewTreeObserver

fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe,
    action: () -> Boolean
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = action,
        overrideStrategy
    )
}

@SuppressLint("OnPreDrawListenerIssue")
internal class OverridableOnPreDrawListener @JvmOverloads constructor(
    private val delegate: ViewTreeObserver.OnPreDrawListener,
    private val overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe
) : ViewTreeObserver.OnPreDrawListener {

    override fun onPreDraw(): Boolean {
        val proceed = delegate.onPreDraw()
        return overrideStrategy.overrideDrawingPass(delegate, proceed)
    }
}
