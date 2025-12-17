package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Configuration(
    @param:JsonProperty("mode")
    val mode: GenerationMode,
    @param:JsonProperty("schema")
    val schemaPath: String,
    @param:JsonProperty("input")
    val inputPath: String,
    @param:JsonProperty("output")
    val outputPath: String,
    @param:JsonProperty("package")
    val packageName: String?
)
