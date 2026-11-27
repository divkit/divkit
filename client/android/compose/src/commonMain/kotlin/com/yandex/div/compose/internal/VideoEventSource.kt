package com.yandex.div.compose.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.coroutines.flow.Flow

@InternalApi
public interface VideoEventSource {
    public val videoEvents: Flow<Event>

    public sealed interface Event {
        public data object Play : Event
        public data object Pause : Event
        public data object Buffering : Event
        public data object End : Event
        public data class Fatal(val error: Throwable? = null) : Event
    }
}
