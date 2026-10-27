package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithOptionalComplexProperty

@Generated
internal class WithOptionalComplexPropertySerializer : EntitySerializer<WithOptionalComplexProperty>(WithOptionalComplexProperty::class.java, "entity_with_optional_complex_property") {
    override fun writeFields(value: WithOptionalComplexProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}

@Generated
internal class WithOptionalComplexPropertyComplexPropertySerializer : EntitySerializer<WithOptionalComplexProperty.ComplexProperty>(WithOptionalComplexProperty.ComplexProperty::class.java) {
    override fun writeFields(value: WithOptionalComplexProperty.ComplexProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
