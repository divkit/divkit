package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithOptionalStringEnumProperty

@Generated
internal class WithOptionalStringEnumPropertySerializer : EntitySerializer<WithOptionalStringEnumProperty>(WithOptionalStringEnumProperty::class.java, "entity_with_optional_string_enum_property") {
    override fun writeFields(value: WithOptionalStringEnumProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}
