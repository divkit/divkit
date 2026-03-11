package com.yandex.div.internal.view

import android.view.ViewTreeObserver.OnPreDrawListener

fun interface DrawingPassOverrideStrategy {

    fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean

    object NoOp : DrawingPassOverrideStrategy {
        override fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean = proceed
    }

    object Safe : SafeDrawingPassOverrideStrategy()
}
