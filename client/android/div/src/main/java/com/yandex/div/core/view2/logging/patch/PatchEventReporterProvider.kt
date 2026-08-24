package com.yandex.div.core.view2.logging.patch

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.util.binding.BindingThread
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Log
import com.yandex.div2.DivPatch
import javax.inject.Inject

@DivViewScope
@BindingThread
internal class PatchEventReporterProvider @Inject constructor(
    private val div2View: Div2View,
) {
    fun get(patch: DivPatch): PatchEventReporter {
        return if (Log.isEnabled) {
            PatchEventReporterImpl(div2View, patch)
        } else {
            PatchEventReporter.STUB
        }
    }
}
