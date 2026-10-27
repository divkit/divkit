package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Blur

@Generated
internal class BlurSerializer : EntitySerializer<Blur>(Blur::class.java, "blur") {
    override fun writeFields(value: Blur, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(RADIUS, properties.radius, provider)
    }

    private companion object {
        val RADIUS = PropertyName("radius")
    }
}
