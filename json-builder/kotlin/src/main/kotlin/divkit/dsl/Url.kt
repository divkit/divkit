package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.scope.DivScope
import kotlin.String
import kotlin.jvm.JvmInline
import kotlin.text.Regex

@JvmInline
value class Url internal constructor(
    @JsonValue
    internal val value: String,
) {
    init {
        require(pattern.matchEntire(value) != null) {
            "Malformed uri string: $value"
        }
    }

    override fun toString() = value

    companion object {
        private val pattern: Regex =
            Regex("(.+://[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|{}]+|^(?![\\s\\S]))")

        fun create(url: String): Url = Url(url)
    }
}

fun DivScope.url(url: String): Url = Url(url)
