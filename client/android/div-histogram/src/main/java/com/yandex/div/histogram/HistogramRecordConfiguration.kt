package com.yandex.div.histogram

import com.yandex.div.core.annotations.PublicApi
import javax.inject.Provider

@PublicApi
interface HistogramRecordConfiguration {
    val isColdRecordingEnabled: Boolean
    val isCoolRecordingEnabled: Boolean
    val isWarmRecordingEnabled: Boolean
    val isSizeRecordingEnabled: Boolean
    val renderConfiguration: Provider<RenderConfiguration>
}
