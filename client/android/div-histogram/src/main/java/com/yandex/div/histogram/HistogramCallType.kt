package com.yandex.div.histogram

import androidx.annotation.StringDef

@StringDef(
    value = [
        HistogramCallType.CALL_TYPE_COLD,
        HistogramCallType.CALL_TYPE_COOL,
        HistogramCallType.CALL_TYPE_WARM,
    ]
)
annotation class HistogramCallType {

    companion object {
        const val CALL_TYPE_COLD = "Cold"
        const val CALL_TYPE_COOL = "Cool"
        const val CALL_TYPE_WARM = "Warm"
    }
}
