package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class Color internal constructor(
    @JsonValue
    val value: String,
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

fun color(argb: String): Color = Color(argb)
