package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.SolidBackground

@Generated
internal class SolidBackgroundSerializer : EntitySerializer<SolidBackground>(SolidBackground::class.java, "solid") {
    override fun writeFields(value: SolidBackground, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
    }
}
