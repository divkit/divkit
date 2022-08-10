package com.yandex.div.legacy

import com.yandex.div.DivBaseBlock
import com.yandex.div.legacy.dagger.DivLegacyScope
import com.yandex.div.legacy.view.DivView
import com.yandex.metrica.IReporterInternal
import dagger.Lazy
import javax.inject.Inject

@DivLegacyScope
internal class DivInternalLogger @Inject constructor(
    private val reporterLazy: Lazy<IReporterInternal>
) {

    private val reporter: IReporterInternal
        get() = reporterLazy.get()

    fun logViewBuildingFailure(divView: DivView, data: DivBaseBlock) {
        val params = mapOf<String, Any>(
            "div view id" to divView.divTag.id,
            "div block id" to data.blockId
        )
        reporter.reportEvent(EVENT_BUILDING_FAILURE, params)
    }

    companion object {
        private const val EVENT_BUILDING_FAILURE = "DIV_VIEW_BUILDING_FAILURE"
    }
}
