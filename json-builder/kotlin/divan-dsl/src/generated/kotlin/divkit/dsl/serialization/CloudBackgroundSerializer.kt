package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.CloudBackground

@Generated
internal class CloudBackgroundSerializer : EntitySerializer<CloudBackground>(CloudBackground::class.java, "cloud") {
    override fun writeFields(value: CloudBackground, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(CORNER_RADIUS, properties.cornerRadius, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val CORNER_RADIUS = PropertyName("corner_radius")
        val PADDINGS = PropertyName("paddings")
    }
}
