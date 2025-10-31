package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.Nulls

data class Signatures(
    @param:JsonProperty("signatures")
    val signatures: List<Signature>,
)

data class Signature(
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("description")
    val description: String?,
    @param:JsonProperty("arguments")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    val arguments: List<Argument> = emptyList(),
    @param:JsonProperty("return_type")
    val returnType: Type,
    @param:JsonProperty("is_method", defaultValue = "false")
    val isMethod: Boolean,
    @param:JsonProperty("platforms")
    val platforms: List<Platform>,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Signature

        if (name != other.name) return false
        if (arguments != other.arguments) return false
        if (returnType != other.returnType) return false
        if (isMethod != other.isMethod) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + arguments.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + isMethod.hashCode()
        return result
    }
}

data class Argument(
    @param:JsonProperty("type")
    val type: Type,
    @param:JsonProperty("description")
    val description: String?,
    @param:JsonProperty("is_vararg", defaultValue = "false")
    val isVararg: Boolean,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Argument

        if (type != other.type) return false
        if (isVararg != other.isVararg) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + isVararg.hashCode()
        return result
    }
}

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
