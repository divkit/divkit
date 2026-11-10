package com.yandex.div.compose.histogram

import com.yandex.div.core.histogram.NoOpHistogramBridge

internal object DisabledHistogramConfiguration : DivHistogramConfiguration {
    override val isEnabled = false
    override val componentName = ""
    override val histogramBridge = NoOpHistogramBridge()
}
