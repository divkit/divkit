package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class Signatures(
    @param:JsonProperty("signatures")
    val signatures: List<Signature>,
)

data class Signature(
    @param:JsonProperty("name")
    val name: String?,
    @param:JsonProperty("function_name")
    val functionName: String,
    @param:JsonProperty("doc")
    val doc: String?,
    @param:JsonProperty("arguments")
    val argumentsRaw: List<Argument>?,
    @param:JsonProperty("result_type")
    val resultType: Type,
    @param:JsonProperty("platforms")
    val platforms: List<Platform>,
) {
    val arguments: List<Argument>
        get() = argumentsRaw ?: emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Signature

        if (functionName != other.functionName) return false
        if (argumentsRaw != other.argumentsRaw) return false

        return true
    }

    override fun hashCode(): Int {
        var result = functionName.hashCode()
        result = 31 * result + argumentsRaw.hashCode()
        return result
    }
}

data class Argument(
    @param:JsonProperty("type")
    val type: Type,
    @param:JsonProperty("doc")
    val doc: String?,
    @param:JsonProperty("vararg", defaultValue = "false")
    val vararg: Boolean,
)

enum class Type(
    @JsonValue
    val serializedName: String,
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
        private val name2value: Map<String, Type> = entries.associateBy { it.serializedName }

        @JvmStatic
        @JsonCreator
        fun fromValue(serializedName: String): Type {
            return name2value.get(serializedName) ?: throw IllegalArgumentException(serializedName)
        }
    }
}

enum class Platform(
    @JsonValue
    val serializedName: String,
) {
    ANDROID("android"),
    IOS("ios"),
    FLUTTER("flutter"),
    WEB("web");

    companion object {
        private val name2value: Map<String, Platform> = entries.associateBy { it.serializedName }

        @JvmStatic
        @JsonCreator
        fun fromValue(serializedName: String): Platform {
            return name2value.get(serializedName) ?: throw IllegalArgumentException(serializedName)
        }
    }
}
