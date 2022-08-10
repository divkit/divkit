package com.yandex.div.histogram

import com.yandex.div.histogram.HistogramCallType.Companion.CALL_TYPE_COLD
import com.yandex.div.histogram.HistogramCallType.Companion.CALL_TYPE_COOL
import com.yandex.div.histogram.HistogramCallType.Companion.CALL_TYPE_WARM
import javax.inject.Provider

class HistogramCallTypeProvider(
    private val histogramColdTypeChecker: () -> HistogramColdTypeChecker
) : HistogramCallTypeChecker() {

    /**
     * @return histogram call type name:
     * [CALL_TYPE_COLD] - if it is the first reporting in application lifetime.
     * [CALL_TYPE_COOL] - if it is the first reporting in [Div2Component] lifetime.
     * [CALL_TYPE_WARM] - all other cases.
     */
    @HistogramCallType
    fun getHistogramCallType(histogramName: String): String {
        if (histogramColdTypeChecker().addReported(histogramName)) {
            addReported(histogramName)
            return CALL_TYPE_COLD
        }
        if (addReported(histogramName)) {
            return CALL_TYPE_COOL
        }
        return CALL_TYPE_WARM
    }
}
