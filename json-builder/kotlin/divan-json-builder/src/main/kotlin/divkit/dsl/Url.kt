package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.scope.DivScope
import kotlin.String
import kotlin.jvm.JvmInline

@JvmInline
value class Url internal constructor(
    @JsonValue
    internal val value: String,
) {
    override fun toString() = value

    companion object {
        fun create(url: String): Url = Url(url)
    }
}

fun DivScope.url(url: String): Url = Url(url)
