package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TextRangeMaskSolid

@Generated
internal class TextRangeMaskSolidSerializer : EntitySerializer<TextRangeMaskSolid>(TextRangeMaskSolid::class.java, "solid") {
    override fun writeFields(value: TextRangeMaskSolid, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val IS_ENABLED = PropertyName("is_enabled")
    }
}
