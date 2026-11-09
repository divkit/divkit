package com.yandex.div.evaluable.types

import java.net.MalformedURLException
import java.net.URL
import kotlin.jvm.JvmInline

@JvmInline
public value class Url(public val value: String) {

    override fun toString(): String = value

    public companion object {

        @Throws(IllegalArgumentException::class)
        public fun from(urlString: String): Url {
            if (isValid(urlString)) return Url(urlString)
            throw IllegalArgumentException("Invalid url $urlString")
        }

        private fun isValid(urlString: String): Boolean {
            return try {
                URL(urlString)
                true
            } catch (e: MalformedURLException) {
                false
            }
        }
    }
}
