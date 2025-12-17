package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Argument(
    @param:JsonProperty("type")
    val type: Type,
    @param:JsonProperty("description")
    val description: Text,
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
