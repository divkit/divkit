package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithArrayOfEnums

@Generated
internal class WithArrayOfEnumsSerializer : EntitySerializer<WithArrayOfEnums>(WithArrayOfEnums::class.java, "entity_with_array_of_enums") {
    override fun writeFields(value: WithArrayOfEnums, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ITEMS = PropertyName("items")
    }
}
