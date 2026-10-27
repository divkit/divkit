package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Stroke

@Generated
internal class StrokeSerializer : EntitySerializer<Stroke>(Stroke::class.java) {
    override fun writeFields(value: Stroke, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(STYLE, properties.style, provider)
        gen.writeProperty(UNIT, properties.unit, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val STYLE = PropertyName("style")
        val UNIT = PropertyName("unit")
        val WIDTH = PropertyName("width")
    }
}
