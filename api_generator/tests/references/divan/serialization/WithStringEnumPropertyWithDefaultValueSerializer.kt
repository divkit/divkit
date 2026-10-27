package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithStringEnumPropertyWithDefaultValue

@Generated
internal class WithStringEnumPropertyWithDefaultValueSerializer : EntitySerializer<WithStringEnumPropertyWithDefaultValue>(WithStringEnumPropertyWithDefaultValue::class.java, "entity_with_string_enum_property_with_default_value") {
    override fun writeFields(value: WithStringEnumPropertyWithDefaultValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
