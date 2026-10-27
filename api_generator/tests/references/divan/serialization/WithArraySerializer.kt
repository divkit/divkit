package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithArray

@Generated
internal class WithArraySerializer : EntitySerializer<WithArray>(WithArray::class.java, "entity_with_array") {
    override fun writeFields(value: WithArray, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ARRAY, properties.array, provider)
    }

    private companion object {
        val ARRAY = PropertyName("array")
    }
}
