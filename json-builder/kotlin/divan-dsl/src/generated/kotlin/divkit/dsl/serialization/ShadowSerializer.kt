package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Shadow

@Generated
internal class ShadowSerializer : EntitySerializer<Shadow>(Shadow::class.java) {
    override fun writeFields(value: Shadow, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(BLUR, properties.blur, provider)
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(OFFSET, properties.offset, provider)
    }

    private companion object {
        val ALPHA = PropertyName("alpha")
        val BLUR = PropertyName("blur")
        val COLOR = PropertyName("color")
        val OFFSET = PropertyName("offset")
    }
}
