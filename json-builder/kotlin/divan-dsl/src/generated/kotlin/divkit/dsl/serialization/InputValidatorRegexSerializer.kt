package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InputValidatorRegex

@Generated
internal class InputValidatorRegexSerializer : EntitySerializer<InputValidatorRegex>(InputValidatorRegex::class.java, "regex") {
    override fun writeFields(value: InputValidatorRegex, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALLOW_EMPTY, properties.allowEmpty, provider)
        gen.writeProperty(LABEL_ID, properties.labelId, provider)
        gen.writeProperty(PATTERN, properties.pattern, provider)
        gen.writeProperty(VARIABLE, properties.variable, provider)
    }

    private companion object {
        val ALLOW_EMPTY = PropertyName("allow_empty")
        val LABEL_ID = PropertyName("label_id")
        val PATTERN = PropertyName("pattern")
        val VARIABLE = PropertyName("variable")
    }
}
