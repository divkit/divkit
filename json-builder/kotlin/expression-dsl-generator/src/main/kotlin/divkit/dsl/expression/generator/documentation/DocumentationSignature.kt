package divkit.dsl.expression.generator.documentation

import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.model.Type
import divkit.dsl.expression.generator.util.RESERVED_METHOD_NAMES
import divkit.dsl.expression.generator.util.resolveKotlinArgumentType
import divkit.dsl.expression.generator.util.resolveKotlinType

data class DocumentationSignature(
    val name: String,
    val description: LocalizedText,
    val arguments: List<DocumentationArgument>,
    val returnType: Type,
    val isMethod: Boolean,
) {

    private val kotlinSignature: String by lazy { buildKotlinSignature() }

    private fun buildKotlinSignature(): String {
        val ktArguments = arguments.joinToString { argument ->
            "Expression<${argument.type.resolveKotlinArgumentType()}>"
        }
        val ktReturnType = "Expression<${returnType.resolveKotlinType()}>"
        return "$name($ktArguments): $ktReturnType"
    }

    override fun toString() = kotlinSignature

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentationSignature

        if (name != other.name) return false
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

fun DocumentationSignature.supportsExtension(): Boolean {
    return arguments.size == 1 && !arguments[0].isVararg && name !in RESERVED_METHOD_NAMES
}
