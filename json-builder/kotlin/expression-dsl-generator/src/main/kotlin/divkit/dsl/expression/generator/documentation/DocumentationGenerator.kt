package divkit.dsl.expression.generator.documentation

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import divkit.dsl.expression.generator.Generator
import divkit.dsl.expression.generator.JsonFileCollector
import divkit.dsl.expression.generator.model.Signature
import divkit.dsl.expression.generator.model.SignatureSet
import java.io.File

class DocumentationGenerator(
    private val schemaFile: File,
    private val inputDir: File,
    private val outputDir: File,
) : Generator {

    private val objectMapper = ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val fileCollector = JsonFileCollector(signaturesSchema())
    private val preprocessor = DocumentationPreprocessor()
    private val codegen = DocumentationCodegen(outputDir)

    private fun signaturesSchema(): JsonSchema {
        val jsonSchema = objectMapper.readTree(schemaFile)
        val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        return jsonSchemaFactory.getSchema(jsonSchema)
    }

    override fun generate() {
        val jsonNodes = fileCollector.collectFiles(inputDir)
        val mappedSignatures = jsonNodes.associateWith { file ->
            objectMapper.readValue(file, SignatureSet::class.java)
        }
        val signatures = mappedSignatures.flatMap { (_, signatures) -> signatures.signatures }

        verifyNoDuplicates(signatures)
        println("Got ${signatures.size} signatures for documentation generations.")

        val documentationSignatures = preprocessor.preprocess(mappedSignatures).sortedBy { signature ->
            signature.toString()
        }
        codegen.generateCode(documentationSignatures, "functions-en.md") { english }
        codegen.generateCode(documentationSignatures, "functions-ru.md") { russian }
    }

    private fun verifyNoDuplicates(signatures: List<Signature>) {
        val duplicates = signatures.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            val fullSignatures = duplicates.keys.joinToString { signature ->
                (signature.name + "(" + signature.arguments.joinToString(",") { argument ->
                    argument.type.value
                } + ")\n")
            }
            throw IllegalStateException("Found duplicated signatures:\n$fullSignatures")
        }
    }
}
