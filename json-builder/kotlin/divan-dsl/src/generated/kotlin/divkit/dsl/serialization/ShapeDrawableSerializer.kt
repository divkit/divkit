package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ShapeDrawable

@Generated
internal class ShapeDrawableSerializer : EntitySerializer<ShapeDrawable>(ShapeDrawable::class.java, "shape_drawable") {
    override fun writeFields(value: ShapeDrawable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(SHAPE, properties.shape, provider)
        gen.writeProperty(STROKE, properties.stroke, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val SHAPE = PropertyName("shape")
        val STROKE = PropertyName("stroke")
    }
}
