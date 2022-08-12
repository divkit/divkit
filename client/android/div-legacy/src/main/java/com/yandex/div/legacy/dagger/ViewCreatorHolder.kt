package com.yandex.div.legacy.dagger

import com.yandex.div.core.histogram.CpuUsageHistogramReporter
import com.yandex.div.view.pooling.ViewCreator

internal object ViewCreatorHolder {
    @JvmField
    val VIEW_CREATOR = ViewCreator(CpuUsageHistogramReporter.NoOp())
}