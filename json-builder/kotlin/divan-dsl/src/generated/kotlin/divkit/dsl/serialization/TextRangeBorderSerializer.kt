package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TextRangeBorder

@Generated
internal class TextRangeBorderSerializer : EntitySerializer<TextRangeBorder>(TextRangeBorder::class.java) {
    override fun writeFields(value: TextRangeBorder, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CORNER_RADIUS, properties.cornerRadius, provider)
        gen.writeProperty(STROKE, properties.stroke, provider)
    }

    private companion object {
        val CORNER_RADIUS = PropertyName("corner_radius")
        val STROKE = PropertyName("stroke")
    }
}
