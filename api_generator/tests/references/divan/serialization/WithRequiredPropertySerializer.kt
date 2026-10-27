package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithRequiredProperty

@Generated
internal class WithRequiredPropertySerializer : EntitySerializer<WithRequiredProperty>(WithRequiredProperty::class.java, "entity_with_required_property") {
    override fun writeFields(value: WithRequiredProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}
