package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.CircleShape

@Generated
internal class CircleShapeSerializer : EntitySerializer<CircleShape>(CircleShape::class.java, "circle") {
    override fun writeFields(value: CircleShape, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BACKGROUND_COLOR, properties.backgroundColor, provider)
        gen.writeProperty(RADIUS, properties.radius, provider)
        gen.writeProperty(STROKE, properties.stroke, provider)
    }

    private companion object {
        val BACKGROUND_COLOR = PropertyName("background_color")
        val RADIUS = PropertyName("radius")
        val STROKE = PropertyName("stroke")
    }
}
