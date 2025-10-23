package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.Argument
import divkit.dsl.expression.generator.model.Signature

class DivkitSignaturesPreprocessor {
    fun toCodegenSignatures(signatures: List<Signature>): List<CodegenSignature> {
        return signatures.map { signature ->
            CodegenSignature(
                functionName = signature.functionName,
                doc = signature.doc ?: "No documentation",
                arguments = signature.arguments.map { it.toCodegenArgument() },
                resultType = signature.resultType,
            )
        }.toSet().toList()
    }
}

private fun Argument.toCodegenArgument(): CodegenArgument {
    return CodegenArgument(
        type = type,
        doc = doc ?: "no documentation",
        vararg = vararg,
    )
}
