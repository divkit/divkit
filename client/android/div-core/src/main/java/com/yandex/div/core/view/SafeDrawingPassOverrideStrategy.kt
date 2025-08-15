package com.yandex.div.core.view

import android.view.ViewTreeObserver.OnPreDrawListener
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public open class SafeDrawingPassOverrideStrategy : DrawingPassOverrideStrategy {

    public var frameCancelLimit: Int = DEFAULT_FRAME_CANCEL_LIMIT
        set(value) {
            if (field != value) {
                field = value
                frameCancelCount = 0
            }
        }

    private var frameCancelCount = 0

    override fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean {
        if (proceed) {
            frameCancelCount = 0
            return true
        } else if (frameCancelCount < frameCancelLimit) {
            frameCancelCount++
            onFrameCancelled(listener, frameCancelCount)
            return false
        } else if (frameCancelCount == frameCancelLimit) {
            frameCancelCount++
            onFrameCancelLimitExceeded(listener, frameCancelCount)
            return true
        } else {
            return true
        }
    }

    protected open fun onFrameCancelled(listener: OnPreDrawListener, frameCancelCount: Int): Unit = Unit

    protected open fun onFrameCancelLimitExceeded(listener: OnPreDrawListener, frameCancelCount: Int): Unit = Unit

    private companion object {
        const val DEFAULT_FRAME_CANCEL_LIMIT: Int = 3
    }
}
