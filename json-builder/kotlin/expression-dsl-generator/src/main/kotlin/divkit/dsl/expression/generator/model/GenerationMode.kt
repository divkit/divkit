package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class GenerationMode(
    @JsonValue
    val value: String,
) {
    FUNCTIONS("functions"),
    DOCUMENTATION("documentation");

    override fun toString(): String {
        return value
    }

    companion object {

        private val mapping: Map<String, GenerationMode> = entries.associateBy { it.value }

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): GenerationMode {
            return mapping[value] ?: throw IllegalArgumentException(value)
        }
    }
}
