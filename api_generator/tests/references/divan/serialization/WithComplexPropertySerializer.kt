package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithComplexProperty

@Generated
internal class WithComplexPropertySerializer : EntitySerializer<WithComplexProperty>(WithComplexProperty::class.java, "entity_with_complex_property") {
    override fun writeFields(value: WithComplexProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}

@Generated
internal class WithComplexPropertyComplexPropertySerializer : EntitySerializer<WithComplexProperty.ComplexProperty>(WithComplexProperty.ComplexProperty::class.java) {
    override fun writeFields(value: WithComplexProperty.ComplexProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
