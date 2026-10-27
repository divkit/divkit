package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ContentText

@Generated
internal class ContentTextSerializer : EntitySerializer<ContentText>(ContentText::class.java, "text") {
    override fun writeFields(value: ContentText, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
