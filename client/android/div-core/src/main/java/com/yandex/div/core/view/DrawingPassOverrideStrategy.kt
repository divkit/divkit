package com.yandex.div.core.view

import android.view.ViewTreeObserver.OnPreDrawListener
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun interface DrawingPassOverrideStrategy {

    public fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean

    @InternalApi
    public object NoOp : DrawingPassOverrideStrategy {
        override fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean = proceed
    }

    @InternalApi
    public object Safe : SafeDrawingPassOverrideStrategy()
}
