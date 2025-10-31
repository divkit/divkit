package divkit.dsl.expression.generator

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import divkit.dsl.expression.generator.model.Signature
import divkit.dsl.expression.generator.model.Signatures
import java.io.File

class DivkitFunctionsGenerator(
    private val outputDir: File,
    private val inputDir: File,
    private val packageName: String,
) {
    private val objectMapper = ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private val signaturesFilesCollector = DivkitSignaturesFilesCollector()
    private val codegen = DivkitCodegen()

    fun generate() {
        val jsonNodes = signaturesFilesCollector.collectSignaturesFiles(inputDir)
        val signatures = jsonNodes.map { file ->
            objectMapper.readValue(file, Signatures::class.java)
        }.map { signatures ->
            signatures.signatures
        }.flatten()

        verifyNoDuplicates(signatures)
        println("Got ${signatures.size} signatures for functions generations.")
        codegen.generateCode(signatures, packageName, outputDir)
    }

    private fun verifyNoDuplicates(signatures: List<Signature>) {
        val duplicates = signatures.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            val fullSignatures = duplicates.keys.joinToString { signature ->
                (signature.name + "(" + signature.arguments.joinToString(",") { argument ->
                    argument.type.serializedName
                } + ")\n")
            }
            throw IllegalStateException("Found duplicated signatures:\n$fullSignatures")
        }
    }
}
