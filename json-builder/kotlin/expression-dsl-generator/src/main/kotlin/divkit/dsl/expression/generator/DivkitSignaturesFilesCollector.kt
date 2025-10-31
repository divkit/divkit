package divkit.dsl.expression.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class DivkitSignaturesFilesCollector {
    private val schema: JsonSchema
    private val objectMapper = ObjectMapper()

    init {
        val resource = javaClass.getResource("/schema.json")
            ?: throw IllegalStateException("Absent schema.json!")
        val jsonSchema = objectMapper.readTree(resource)
        val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        schema = jsonSchemaFactory.getSchema(jsonSchema)
    }

    fun collectSignaturesFiles(inputDir: File): List<File> {
        return Files.walk(Path.of(inputDir.toURI()), FileVisitOption.FOLLOW_LINKS)
            .asSequence()
            .map {
                it.toFile()
            }
            .filter {
                it.extension == "json"
            }
            .associateWith {
                try {
                    objectMapper.readTree(it)
                } catch (e: IOException) {
                    println("Failed to parse JSON file ${it.name} due to exception.")
                    e.printStackTrace()
                    null
                }
            }
            .mapNotNull { (file: File, node: JsonNode?) ->
                if (node == null) {
                    return@mapNotNull null
                }
                val errors = schema.validate(node)
                if (errors.isNotEmpty() && node.has("signatures")) {
                    println("Signatures file ${file.name} doesn't follow JSON Schema. Errors:")
                    errors.forEach {
                        println(it.message)
                    }
                }
                file.takeIf { errors.isEmpty() }
            }
    }
}
