package com.yandex.div.evaluable.types

import java.net.MalformedURLException
import java.net.URL

@JvmInline
value class Url(val value: String) {

    override fun toString() = value

    companion object {

        @Throws(IllegalArgumentException::class)
        fun from(urlString: String): Url {
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
