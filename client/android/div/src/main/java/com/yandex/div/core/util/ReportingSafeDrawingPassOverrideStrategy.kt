package com.yandex.div.core.util

import android.view.ViewTreeObserver.OnPreDrawListener
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view.SafeDrawingPassOverrideStrategy
import com.yandex.div.core.view2.Div2View
import javax.inject.Inject

@DivViewScope
internal class ReportingSafeDrawingPassOverrideStrategy @Inject constructor(
    private val divView: Div2View,
    private val logger: Div2Logger,
) : SafeDrawingPassOverrideStrategy() {

    override fun onFrameCancelled(listener: OnPreDrawListener, frameCancelCount: Int) {
        logger.logFrameCancelled(divView, "Frame cancelled by $listener")
    }

    override fun onFrameCancelLimitExceeded(listener: OnPreDrawListener, frameCancelCount: Int) {
        logger.logFrameCancelLimitExceeded(
            divView,
            "Frame cancellation limit exceeded by $listener. Forcing frame drawing."
        )
    }
}
