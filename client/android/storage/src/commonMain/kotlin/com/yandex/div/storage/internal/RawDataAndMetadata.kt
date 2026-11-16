package com.yandex.div.storage.internal

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public interface RawDataAndMetadata<T : Any> {
    public val id: String
    public val divData: T
    public val metadata: T?

    public class Ready<T : Any>(
        override val id: String,
        override val divData: T,
        override val metadata: T?,
    ) : RawDataAndMetadata<T>

    public companion object {
        public operator fun <T : Any> invoke(
            id: String,
            divData: T,
            metadata: T? = null,
        ): RawDataAndMetadata<T> = Ready(id, divData, metadata)
    }
}
