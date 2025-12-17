package divkit.dsl.expression.generator.function

import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.model.Type
import divkit.dsl.expression.generator.util.resolveKotlinArgumentType

data class FunctionArgument(
    val type: Type,
    val description: LocalizedText,
    val isVararg: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FunctionArgument

        if (isVararg != other.isVararg) return false
        if (type.resolveKotlinArgumentType() != type.resolveKotlinArgumentType()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.resolveKotlinArgumentType().hashCode()
        result = 31 * result + isVararg.hashCode()
        return result
    }
}
