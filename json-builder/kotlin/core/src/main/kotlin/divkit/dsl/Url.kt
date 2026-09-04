package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class Url internal constructor(
    @JsonValue val value: String,
) {
    override fun toString(): String = value

    companion object {
        @Deprecated(message = "Use function `url(url: String)`", replaceWith = ReplaceWith("url"))
        fun create(url: String): Url = Url(url)
    }
}

fun url(url: String): Url = Url(url)

fun String.asUrl(): Url = Url(this)
