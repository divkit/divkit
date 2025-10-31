package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.Argument
import divkit.dsl.expression.generator.model.Signature

class DivkitSignaturesPreprocessor {
    fun toCodegenSignatures(signatures: List<Signature>): List<CodegenSignature> {
        return signatures.map { signature ->
            CodegenSignature(
                name = signature.name,
                description = signature.description ?: "No documentation",
                arguments = signature.arguments.map { it.toCodegenArgument() },
                returnType = signature.returnType,
                isMethod = signature.isMethod
            )
        }.toSet().toList()
    }
}

private fun Argument.toCodegenArgument(): CodegenArgument {
    return CodegenArgument(
        type = type,
        description = description ?: "no documentation",
        isVararg = isVararg,
    )
}
