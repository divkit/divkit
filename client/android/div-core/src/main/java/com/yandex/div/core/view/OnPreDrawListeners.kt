@file:JvmName("OnPreDrawListeners")

package com.yandex.div.core.view

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe,
    action: () -> Boolean
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = action,
        overrideStrategy
    )
}

@InternalApi
public fun onPreDrawListener(
    overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe,
    delegate: ViewTreeObserver.OnPreDrawListener
): ViewTreeObserver.OnPreDrawListener {
    return OverridableOnPreDrawListener(
        delegate = delegate,
        overrideStrategy
    )
}

@InternalApi
@SuppressLint("OnPreDrawListenerIssue")
public class OverridableOnPreDrawListener @JvmOverloads constructor(
    private val delegate: ViewTreeObserver.OnPreDrawListener,
    private val overrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe
) : ViewTreeObserver.OnPreDrawListener {

    override fun onPreDraw(): Boolean {
        val proceed = delegate.onPreDraw()
        return overrideStrategy.overrideDrawingPass(delegate, proceed)
    }
}
