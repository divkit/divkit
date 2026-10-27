package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithStringArrayProperty

@Generated
internal class WithStringArrayPropertySerializer : EntitySerializer<WithStringArrayProperty>(WithStringArrayProperty::class.java, "entity_with_string_array_property") {
    override fun writeFields(value: WithStringArrayProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ARRAY, properties.array, provider)
    }

    private companion object {
        val ARRAY = PropertyName("array")
    }
}
