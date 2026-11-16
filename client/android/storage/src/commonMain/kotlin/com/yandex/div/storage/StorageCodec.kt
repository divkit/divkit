package com.yandex.div.storage

import com.yandex.div.core.annotations.InternalApi

/**
 * Serialization boundary for stored payloads. The storage owns no model or JSON dependency.
 */
@InternalApi
public interface StorageCodec<T : Any> {
    public fun encode(value: T): ByteArray
    public fun decode(bytes: ByteArray): T

    public companion object {
        public val BYTES: StorageCodec<ByteArray> = object : StorageCodec<ByteArray> {
            override fun encode(value: ByteArray): ByteArray = value
            override fun decode(bytes: ByteArray): ByteArray = bytes
        }
    }
}
