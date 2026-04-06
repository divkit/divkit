package com.yandex.div.histogram

import javax.inject.Provider

interface HistogramRecordConfiguration {
    val isColdRecordingEnabled: Boolean
    val isCoolRecordingEnabled: Boolean
    val isWarmRecordingEnabled: Boolean
    val isSizeRecordingEnabled: Boolean
    val renderConfiguration: Provider<RenderConfiguration>
}
