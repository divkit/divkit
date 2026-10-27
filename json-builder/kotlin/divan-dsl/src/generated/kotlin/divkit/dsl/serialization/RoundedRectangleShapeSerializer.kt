package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.RoundedRectangleShape

@Generated
internal class RoundedRectangleShapeSerializer : EntitySerializer<RoundedRectangleShape>(RoundedRectangleShape::class.java, "rounded_rectangle") {
    override fun writeFields(value: RoundedRectangleShape, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BACKGROUND_COLOR, properties.backgroundColor, provider)
        gen.writeProperty(CORNER_RADIUS, properties.cornerRadius, provider)
        gen.writeProperty(ITEM_HEIGHT, properties.itemHeight, provider)
        gen.writeProperty(ITEM_WIDTH, properties.itemWidth, provider)
        gen.writeProperty(STROKE, properties.stroke, provider)
    }

    private companion object {
        val BACKGROUND_COLOR = PropertyName("background_color")
        val CORNER_RADIUS = PropertyName("corner_radius")
        val ITEM_HEIGHT = PropertyName("item_height")
        val ITEM_WIDTH = PropertyName("item_width")
        val STROKE = PropertyName("stroke")
    }
}
