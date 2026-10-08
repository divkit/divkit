package com.yandex.div.compose.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.flow.Flow

@InternalApi
interface VideoEventSource {
    val videoEvents: Flow<Event>

    sealed interface Event {
        data object Play : Event
        data object Pause : Event
        data object Buffering : Event
        data object End : Event
        data class Fatal(val error: Throwable? = null) : Event
    }
}
