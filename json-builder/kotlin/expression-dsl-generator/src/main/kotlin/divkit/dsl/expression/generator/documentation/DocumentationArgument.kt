package divkit.dsl.expression.generator.documentation

import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.model.Type
import divkit.dsl.expression.generator.util.resolveKotlinArgumentType

data class DocumentationArgument(
    val type: Type,
    val description: LocalizedText,
    val isVararg: Boolean,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentationArgument

        if (isVararg != other.isVararg) return false
        if (type.resolveKotlinArgumentType() != other.type.resolveKotlinArgumentType()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isVararg.hashCode()
        result = 31 * result + type.resolveKotlinArgumentType().hashCode()
        return result
    }
}
