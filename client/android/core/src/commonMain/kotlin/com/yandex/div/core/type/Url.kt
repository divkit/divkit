package com.yandex.div.core.type

import kotlin.jvm.JvmInline

@JvmInline
public value class Url private constructor(
    private val value: String
) {

    public val scheme: String
        get() = UrlComponents.scheme(value)

    /**
     * Percent-decoded authority, or `null` when the url has no `//` part.
     */
    public val authority: String?
        get() = UrlComponents.authority(value)

    /**
     * Returns the percent-decoded value of the first query parameter with the given name,
     * an empty string when the parameter has no value, or `null` when it's missing.
     * As in `android.net.Uri`, the name is percent-encoded before matching, and `+` in the value is decoded as a space.
     */
    public fun getQueryParameter(name: String): String? = UrlComponents.queryParameter(value, name)

    /**
     * Returns `false` when the parameter value is `false` or `0` ignoring case,
     * [defaultValue] when the parameter is missing, and `true` otherwise.
     */
    public fun getBooleanQueryParameter(name: String, defaultValue: Boolean): Boolean {
        val flag = getQueryParameter(name)?.lowercase() ?: return defaultValue
        return flag != "false" && flag != "0"
    }

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
