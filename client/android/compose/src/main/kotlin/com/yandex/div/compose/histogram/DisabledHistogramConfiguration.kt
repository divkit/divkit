package com.yandex.div.compose.histogram

import com.yandex.div.histogram.NoOpHistogramBridge

internal object DisabledHistogramConfiguration : DivHistogramConfiguration {
    override val isEnabled = false
    override val componentName = ""
    override val histogramBridge = NoOpHistogramBridge()
}
