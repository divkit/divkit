package com.yandex.div.core.widget

import android.view.ViewTreeObserver

internal fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy,
    action: () -> Boolean
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = { action() },
        overrideStrategy
    )
}

internal fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy,
    delegate: ViewTreeObserver.OnPreDrawListener
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = delegate,
        overrideStrategy
    )
}

internal class OverridableOnPreDrawListener(
    private val delegate: ViewTreeObserver.OnPreDrawListener,
    private val overrideStrategy: DrawingPassOverrideStrategy
): ViewTreeObserver.OnPreDrawListener {

    override fun onPreDraw(): Boolean {
        val proceed = delegate.onPreDraw()
        return overrideStrategy.overrideDrawingPass(delegate, proceed)
    }
}
