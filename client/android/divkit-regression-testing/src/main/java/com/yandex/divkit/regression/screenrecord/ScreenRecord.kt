package com.yandex.divkit.regression.screenrecord

import androidx.lifecycle.DefaultLifecycleObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ScreenRecord : DefaultLifecycleObserver {
    val recordState: StateFlow<RecordState>

    enum class RecordState {
        STARTED, STOPPED,
    }

    object Stub : ScreenRecord {
        override val recordState: StateFlow<RecordState> =
            MutableStateFlow(RecordState.STOPPED).asStateFlow()
    }
}
