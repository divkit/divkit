package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithRawArray

@Generated
internal class WithRawArraySerializer : EntitySerializer<WithRawArray>(WithRawArray::class.java, "entity_with_raw_array") {
    override fun writeFields(value: WithRawArray, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ARRAY, properties.array, provider)
    }

    private companion object {
        val ARRAY = PropertyName("array")
    }
}
