package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithComplexPropertyWithDefaultValue

@Generated
internal class WithComplexPropertyWithDefaultValueSerializer : EntitySerializer<WithComplexPropertyWithDefaultValue>(WithComplexPropertyWithDefaultValue::class.java, "entity_with_complex_property_with_default_value") {
    override fun writeFields(value: WithComplexPropertyWithDefaultValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}

@Generated
internal class WithComplexPropertyWithDefaultValueComplexPropertySerializer : EntitySerializer<WithComplexPropertyWithDefaultValue.ComplexProperty>(WithComplexPropertyWithDefaultValue.ComplexProperty::class.java) {
    override fun writeFields(value: WithComplexPropertyWithDefaultValue.ComplexProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
