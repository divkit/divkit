package com.yandex.div.core.view2.logging.bind

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.util.binding.BindingThread
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Log
import com.yandex.div2.DivData
import javax.inject.Inject

@DivViewScope
@BindingThread
internal class BindingEventReporterProvider @Inject constructor(
    private val div2View: Div2View,
) {
    fun get(oldData: DivData?, newData: DivData?): BindingEventReporter {
        return if (Log.isEnabled) {
            BindingEventReporterImpl(div2View, oldData, newData)
        } else {
            BindingEventReporter.STUB
        }
    }
}
