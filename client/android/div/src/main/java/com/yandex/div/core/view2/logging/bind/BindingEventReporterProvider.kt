package com.yandex.div.core.view2.logging.bind

import com.yandex.div.core.util.binding.BindingThread
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Log
import com.yandex.div2.DivData

@BindingThread
internal class BindingEventReporterProvider(
    private val div2View: Div2View,
) {
    fun get(oldData: DivData?, newData: DivData?): BindingEventReporter {
        return if (Log.isEnabled()) {
            BindingEventReporterImpl(div2View, oldData, newData)
        } else {
            BindingEventReporter.STUB
        }
    }
}
