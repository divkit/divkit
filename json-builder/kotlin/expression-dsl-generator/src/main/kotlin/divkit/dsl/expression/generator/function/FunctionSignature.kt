package divkit.dsl.expression.generator.function

import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.model.Type
import divkit.dsl.expression.generator.util.RESERVED_METHOD_NAMES
import divkit.dsl.expression.generator.util.resolveKotlinType

data class FunctionSignature(
    val name: String,
    val description: LocalizedText,
    val arguments: List<FunctionArgument>,
    val returnType: Type,
    val isMethod: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FunctionSignature

        if (name != other.name) return false
        if (arguments.size != other.arguments.size) return false
        arguments.indices.forEach {index ->
            if (arguments[index] != other.arguments[index]) return false
        }
        if (arguments != other.arguments) return false
        if (returnType.resolveKotlinType() != other.returnType.resolveKotlinType()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + arguments.hashCode()
        result = 31 * result + returnType.resolveKotlinType().hashCode()
        return result
    }
}

fun FunctionSignature.supportsExtension(): Boolean {
    return arguments.size == 1 && !arguments[0].isVararg && name !in RESERVED_METHOD_NAMES
}
