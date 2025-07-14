package com.yandex.div.core.util

import android.view.ViewTreeObserver.OnPreDrawListener
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.DrawingPassOverrideStrategy
import javax.inject.Inject

@DivViewScope
internal class SafeDrawingPassOverrideStrategy @Inject constructor(
    private val divView: Div2View,
    private val logger: Div2Logger,
) : DrawingPassOverrideStrategy {

    var frameCancelLimit: Int = DEFAULT_FRAME_CANCEL_LIMIT
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
            logger.logFrameCancelled(divView, "Frame cancelled by $listener")
            return false
        } else if (frameCancelCount == frameCancelLimit) {
            frameCancelCount++
            logger.logFrameCancelLimitExceeded(
                divView,
                "Frame cancellation limit exceeded by $listener. Forcing frame drawing."
            )
            return true
        } else {
            return true
        }
    }

    companion object {
        const val DEFAULT_FRAME_CANCEL_LIMIT = 3
    }
}
