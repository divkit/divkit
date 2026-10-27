package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithJsonProperty

@Generated
internal class WithJsonPropertySerializer : EntitySerializer<WithJsonProperty>(WithJsonProperty::class.java, "entity_with_json_property") {
    override fun writeFields(value: WithJsonProperty, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(JSON_PROPERTY, properties.jsonProperty, provider)
    }

    private companion object {
        val JSON_PROPERTY = PropertyName("json_property")
    }
}
