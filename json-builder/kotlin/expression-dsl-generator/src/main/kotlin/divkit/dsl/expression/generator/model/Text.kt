package divkit.dsl.expression.generator.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(value = LocalizedText::class),
    JsonSubTypes.Type(value = ReferencedText::class)
)
sealed interface Text

data class LocalizedText(
    @param:JsonProperty("en")
    val english: String,
    @param:JsonProperty("ru")
    val russian: String,
) : Text

data class ReferencedText(
    @param:JsonProperty("\$ref")
    val reference: String,
) : Text
