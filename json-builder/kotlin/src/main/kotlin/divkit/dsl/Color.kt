package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.scope.DivScope
import kotlin.String
import kotlin.jvm.JvmInline
import kotlin.text.Regex

@JvmInline
value class Color internal constructor(
    @JsonValue
    internal val value: String,
) {
    init {
        require(pattern.matchEntire(value) != null) {
            "Malformed color string: $value"
        }
    }
    companion object {
        private val pattern: Regex =
            Regex("#([\\dA-Fa-f]{3}|[\\dA-Fa-f]{4}|[\\dA-Fa-f]{6}|[\\dA-Fa-f]{8})")
    }
}

fun DivScope.color(argb: String): Color = Color(argb)
