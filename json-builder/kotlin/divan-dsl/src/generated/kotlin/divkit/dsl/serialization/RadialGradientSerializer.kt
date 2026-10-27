package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.RadialGradient

@Generated
internal class RadialGradientSerializer : EntitySerializer<RadialGradient>(RadialGradient::class.java, "radial_gradient") {
    override fun writeFields(value: RadialGradient, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CENTER_X, properties.centerX, provider)
        gen.writeProperty(CENTER_Y, properties.centerY, provider)
        gen.writeProperty(COLOR_MAP, properties.colorMap, provider)
        gen.writeProperty(COLORS, properties.colors, provider)
        gen.writeProperty(RADIUS, properties.radius, provider)
    }

    private companion object {
        val CENTER_X = PropertyName("center_x")
        val CENTER_Y = PropertyName("center_y")
        val COLOR_MAP = PropertyName("color_map")
        val COLORS = PropertyName("colors")
        val RADIUS = PropertyName("radius")
    }
}

@Generated
internal class RadialGradientColorPointSerializer : EntitySerializer<RadialGradient.ColorPoint>(RadialGradient.ColorPoint::class.java) {
    override fun writeFields(value: RadialGradient.ColorPoint, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(POSITION, properties.position, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val POSITION = PropertyName("position")
    }
}
