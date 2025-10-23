package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.Type

data class CodegenSignature(
    val functionName: String,
    val doc: String,
    val arguments: List<CodegenArgument>,
    val resultType: Type,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodegenSignature

        if (functionName != other.functionName) return false
        if (arguments.size != other.arguments.size) return false
        arguments.indices.forEach {index ->
            if (arguments[index] != other.arguments[index]) return false
        }
        if (arguments != other.arguments) return false
        if (resultType.resolveKotlinType() != other.resultType.resolveKotlinType()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = functionName.hashCode()
        result = 31 * result + arguments.hashCode()
        result = 31 * result + resultType.resolveKotlinType().hashCode()
        return result
    }
}

data class CodegenArgument(
    val type: Type,
    val doc: String,
    val vararg: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodegenArgument

        if (vararg != other.vararg) return false
        if (type.resolveKotlinArgumentType() != type.resolveKotlinArgumentType()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.resolveKotlinArgumentType().hashCode()
        result = 31 * result + vararg.hashCode()
        return result
    }
}
