package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

data class Signature(
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("description")
    val description: Text,
    @param:JsonProperty("arguments")
    @param:JsonSetter(nulls = Nulls.AS_EMPTY)
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

data class SignatureSet(
    @param:JsonProperty("signatures")
    val signatures: List<Signature>,
)
