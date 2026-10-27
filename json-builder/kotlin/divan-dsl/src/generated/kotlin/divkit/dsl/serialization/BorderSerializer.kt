package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Border

@Generated
internal class BorderSerializer : EntitySerializer<Border>(Border::class.java) {
    override fun writeFields(value: Border, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CORNER_RADIUS, properties.cornerRadius, provider)
        gen.writeProperty(CORNERS_RADIUS, properties.cornersRadius, provider)
        gen.writeProperty(HAS_SHADOW, properties.hasShadow, provider)
        gen.writeProperty(SHADOW, properties.shadow, provider)
        gen.writeProperty(STROKE, properties.stroke, provider)
    }

    private companion object {
        val CORNER_RADIUS = PropertyName("corner_radius")
        val CORNERS_RADIUS = PropertyName("corners_radius")
        val HAS_SHADOW = PropertyName("has_shadow")
        val SHADOW = PropertyName("shadow")
        val STROKE = PropertyName("stroke")
    }
}
