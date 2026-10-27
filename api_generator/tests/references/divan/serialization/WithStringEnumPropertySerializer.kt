package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithStringEnumProperty

@Generated
internal class WithStringEnumPropertySerializer : EntitySerializer<WithStringEnumProperty>(WithStringEnumProperty::class.java, "entity_with_string_enum_property") {
    override fun writeFields(value: WithStringEnumProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}
