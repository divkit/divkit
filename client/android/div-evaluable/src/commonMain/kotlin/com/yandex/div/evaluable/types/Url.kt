package com.yandex.div.evaluable.types

import kotlin.jvm.JvmInline

@JvmInline
public value class Url(public val value: String) {

    override fun toString(): String = value

    public companion object {

        @Throws(IllegalArgumentException::class)
        public fun from(urlString: String): Url {
            if (isValidUrl(urlString)) return Url(urlString)
            throw IllegalArgumentException("Invalid url $urlString")
        }
    }
}

internal expect fun isValidUrl(urlString: String): Boolean
