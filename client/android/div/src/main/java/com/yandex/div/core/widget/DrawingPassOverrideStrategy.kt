package com.yandex.div.core.widget

import android.view.ViewTreeObserver.OnPreDrawListener

internal fun interface DrawingPassOverrideStrategy {

    fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean): Boolean

    object Default : DrawingPassOverrideStrategy {
        override fun overrideDrawingPass(listener: OnPreDrawListener, proceed: Boolean) = proceed
    }
}
