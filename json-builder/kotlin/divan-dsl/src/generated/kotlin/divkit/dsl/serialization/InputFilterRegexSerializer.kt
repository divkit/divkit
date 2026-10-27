package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InputFilterRegex

@Generated
internal class InputFilterRegexSerializer : EntitySerializer<InputFilterRegex>(InputFilterRegex::class.java, "regex") {
    override fun writeFields(value: InputFilterRegex, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PATTERN, properties.pattern, provider)
    }

    private companion object {
        val PATTERN = PropertyName("pattern")
    }
}
