package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Type(
    @JsonValue
    val value: String,
) {
    INTEGER("integer"),
    BOOLEAN("boolean"),
    STRING("string"),
    NUMBER("number"),
    COLOR("color"),
    DATETIME("datetime"),
    ARRAY("array"),
    URL("url"),
    DICT("dict");

    companion object {

        private val mapping: Map<String, Type> = entries.associateBy { it.value }

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Type {
            return mapping[value] ?: throw IllegalArgumentException(value)
        }
    }
}
