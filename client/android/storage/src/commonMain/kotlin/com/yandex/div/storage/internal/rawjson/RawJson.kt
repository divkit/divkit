package com.yandex.div.storage.internal.rawjson

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public interface RawJson<T : Any> {
    public val id: String
    public val data: T

    public data class Ready<T : Any>(
        override val id: String,
        override val data: T,
    ) : RawJson<T>

    public companion object {
        public operator fun <T : Any> invoke(id: String, data: T): RawJson<T> = Ready(id, data)
    }
}
