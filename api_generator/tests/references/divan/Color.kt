@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonValue
import divan.annotation.Generated
import divan.scope.DivScope
import kotlin.String
import kotlin.Suppress
import kotlin.jvm.JvmInline
import kotlin.text.Regex

@Generated
@JvmInline
value class Color internal constructor(
    @JsonValue
    internal val value: String,
) {
    init {
        require(argbPattern.matchEntire(value) != null) {
            "Malformed color string: $value"
        }
    }
    companion object {
        private val argbPattern: Regex =
                Regex("#([\\dA-Fa-f]{3}|[\\dA-Fa-f]{4}|[\\dA-Fa-f]{6}|[\\dA-Fa-f]{8})")
    }
}

@Generated
fun DivScope.color(argb: String): Color = Color(argb)
