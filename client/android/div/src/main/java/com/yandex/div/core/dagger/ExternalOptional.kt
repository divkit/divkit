// Copyright 2023 Yandex LLC. All rights reserved.

package com.yandex.div.core.dagger

import com.yandex.yatagan.Optional
import javax.inject.Inject

/**
 * Wrapper for manual [Optional] bindings in yatagan.
 */
class ExternalOptional<T : Any> @Inject constructor(
        val optional: Optional<T>
) {
    companion object {
        /**
         * Creates [ExternalOptional] using [Optional].
         */
        @JvmStatic
        fun <T: Any> wrap(optional: Optional<T>) = ExternalOptional(optional)

        /**
         * Creates an instance with empty [optional].
         */
        @JvmStatic
        fun <T : Any> empty() = ExternalOptional<T>(Optional.empty())

        /**
         * Creates an instance with specified value.
         * @see Optional.of
         */
        @JvmStatic
        fun <T : Any> of(value: T) = ExternalOptional<T>(Optional.of(value))

        /**
         * @see Optional.ofNullable
         */
        @JvmStatic
        fun <T : Any> ofNullable(value: T?): ExternalOptional<T> {
            return if (value != null) of(value) else empty()
        }
    }
}

/**
 * Creates [ExternalOptional] using [Optional].
 */
fun <T : Any> Optional<T>.asExternal() = ExternalOptional(this)
