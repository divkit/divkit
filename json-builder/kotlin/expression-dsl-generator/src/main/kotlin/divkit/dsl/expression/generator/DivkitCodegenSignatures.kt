package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.Type

data class CodegenSignature(
    val name: String,
    val description: String,
    val arguments: List<CodegenArgument>,
    val returnType: Type,
    val isMethod: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodegenSignature

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

data class CodegenArgument(
    val type: Type,
    val description: String,
    val isVararg: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodegenArgument

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
