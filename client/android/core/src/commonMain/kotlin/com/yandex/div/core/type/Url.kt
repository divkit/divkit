package com.yandex.div.core.type

@JvmInline
public value class Url private constructor(
    private val value: String
) {

    override fun toString(): String = value

    public companion object {

        public fun create(urlString: String): Url {
            val result = UrlValidator.validate(urlString)
            if (result.isSuccess) {
                return Url(urlString)
            } else {
                throw IllegalArgumentException("Invalid url $urlString", result.exceptionOrNull())
            }
        }
    }
}
