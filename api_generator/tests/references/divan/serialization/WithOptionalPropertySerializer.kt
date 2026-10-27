package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithOptionalProperty

@Generated
internal class WithOptionalPropertySerializer : EntitySerializer<WithOptionalProperty>(WithOptionalProperty::class.java, "entity_with_optional_property") {
    override fun writeFields(value: WithOptionalProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val PROPERTY = PropertyName("property")
    }
}
