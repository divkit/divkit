package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Transform

@Generated
internal class TransformSerializer : EntitySerializer<Transform>(Transform::class.java) {
    override fun writeFields(value: Transform, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PIVOT_X, properties.pivotX, provider)
        gen.writeProperty(PIVOT_Y, properties.pivotY, provider)
        gen.writeProperty(ROTATION, properties.rotation, provider)
    }

    private companion object {
        val PIVOT_X = PropertyName("pivot_x")
        val PIVOT_Y = PropertyName("pivot_y")
        val ROTATION = PropertyName("rotation")
    }
}
