package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithEntityProperty

@Generated
internal class WithEntityPropertySerializer : EntitySerializer<WithEntityProperty>(WithEntityProperty::class.java, "entity_with_entity_property") {
    override fun writeFields(value: WithEntityProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ENTITY, properties.entity, provider)
    }

    private companion object {
        val ENTITY = PropertyName("entity")
    }
}
