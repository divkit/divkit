package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Platform(
    @JsonValue
    val value: String,
) {
    ANDROID("android"),
    IOS("ios"),
    WEB("web"),
    FLUTTER("flutter");

    companion object {

        private val mapping: Map<String, Platform> = entries.associateBy { it.value }

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Platform {
            return mapping[value] ?: throw IllegalArgumentException(value)
        }
    }
}
