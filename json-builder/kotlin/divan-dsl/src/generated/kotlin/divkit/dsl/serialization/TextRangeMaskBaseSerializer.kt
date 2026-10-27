package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TextRangeMaskBase

@Generated
internal class TextRangeMaskBaseSerializer : EntitySerializer<TextRangeMaskBase>(TextRangeMaskBase::class.java) {
    override fun writeFields(value: TextRangeMaskBase, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
    }

    private companion object {
        val IS_ENABLED = PropertyName("is_enabled")
    }
}
