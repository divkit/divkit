package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.LinearGradient

@Generated
internal class LinearGradientSerializer : EntitySerializer<LinearGradient>(LinearGradient::class.java, "gradient") {
    override fun writeFields(value: LinearGradient, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANGLE, properties.angle, provider)
        gen.writeProperty(COLOR_MAP, properties.colorMap, provider)
        gen.writeProperty(COLORS, properties.colors, provider)
    }

    private companion object {
        val ANGLE = PropertyName("angle")
        val COLOR_MAP = PropertyName("color_map")
        val COLORS = PropertyName("colors")
    }
}

@Generated
internal class LinearGradientColorPointSerializer : EntitySerializer<LinearGradient.ColorPoint>(LinearGradient.ColorPoint::class.java) {
    override fun writeFields(value: LinearGradient.ColorPoint, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(POSITION, properties.position, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val POSITION = PropertyName("position")
    }
}
