@file:JvmName("Main")

package divkit.dsl.expression.generator

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import divkit.dsl.expression.generator.documentation.DocumentationGenerator
import divkit.dsl.expression.generator.function.FunctionGenerator
import divkit.dsl.expression.generator.model.Configuration
import divkit.dsl.expression.generator.model.GenerationMode
import divkit.dsl.expression.generator.util.asFile
import divkit.dsl.expression.generator.util.prepareDirectory
import java.io.File

fun main(args: Array<String>) {
    val configuration = readConfiguration(File(args[0]))
    val generator = getGenerator(configuration)
    generator.generate()
}

private fun readConfiguration(configFile: File): Configuration {
    val objectMapper = ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    return objectMapper.readValue(configFile, Configuration::class.java)
}

private fun getGenerator(configuration: Configuration): Generator {
    return when (val mode = configuration.mode) {
        GenerationMode.FUNCTIONS -> {
            FunctionGenerator(
                schemaFile = configuration.schemaPath.asFile(),
                inputDir = configuration.inputPath.asFile(),
                outputDir = configuration.outputPath.asFile().prepareDirectory(),
                packageName = configuration.packageName ?: throw RuntimeException("Java package name is not specified")
            )
        }

        GenerationMode.DOCUMENTATION -> {
            DocumentationGenerator(
                schemaFile = configuration.schemaPath.asFile(),
                inputDir = configuration.inputPath.asFile(),
                outputDir = configuration.outputPath.asFile().prepareDirectory()
            )
        }
    }
}
