package divkit.dsl.expression.generator.documentation

import divkit.dsl.expression.generator.Preprocessor
import divkit.dsl.expression.generator.ReferenceResolver
import divkit.dsl.expression.generator.model.Argument
import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.model.ReferencedText
import divkit.dsl.expression.generator.model.Signature
import divkit.dsl.expression.generator.model.SignatureSet
import divkit.dsl.expression.generator.model.Text
import java.io.File

class DocumentationPreprocessor : Preprocessor<List<DocumentationSignature>> {

    private val referenceResolver = ReferenceResolver()

    override fun preprocess(signatures: Map<File, SignatureSet>): List<DocumentationSignature> {
        return signatures.flatMap { (location, signatureSet) ->
            signatureSet.signatures.map { preprocessSignature(location, it) }
        }.distinct()
    }

    private fun preprocessSignature(location: File, signature: Signature): DocumentationSignature {
        return DocumentationSignature(
            name = signature.name,
            description = resolveDescription(location, signature.description),
            arguments = signature.arguments.map { argument -> preprocessArgument(location, argument) },
            returnType = signature.returnType,
            isMethod = signature.isMethod
        )
    }

    private fun preprocessArgument(location: File, argument: Argument): DocumentationArgument {
        return DocumentationArgument(
            type = argument.type,
            description = resolveDescription(location, argument.description),
            isVararg = argument.isVararg
        )
    }

    private fun resolveDescription(location: File, description: Text): LocalizedText {
        return when (description) {
            is LocalizedText -> description
            is ReferencedText -> referenceResolver.resolveReference<LocalizedText>(location, description.reference)
        }
    }
}
