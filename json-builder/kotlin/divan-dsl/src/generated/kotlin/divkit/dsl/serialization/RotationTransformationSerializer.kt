package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.RotationTransformation

@Generated
internal class RotationTransformationSerializer : EntitySerializer<RotationTransformation>(RotationTransformation::class.java, "rotation") {
    override fun writeFields(value: RotationTransformation, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANGLE, properties.angle, provider)
        gen.writeProperty(PIVOT_X, properties.pivotX, provider)
        gen.writeProperty(PIVOT_Y, properties.pivotY, provider)
    }

    private companion object {
        val ANGLE = PropertyName("angle")
        val PIVOT_X = PropertyName("pivot_x")
        val PIVOT_Y = PropertyName("pivot_y")
    }
}
