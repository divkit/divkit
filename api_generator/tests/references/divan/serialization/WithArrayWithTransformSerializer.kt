package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithArrayWithTransform

@Generated
internal class WithArrayWithTransformSerializer : EntitySerializer<WithArrayWithTransform>(WithArrayWithTransform::class.java, "entity_with_array_with_transform") {
    override fun writeFields(value: WithArrayWithTransform, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ARRAY, properties.array, provider)
    }

    private companion object {
        val ARRAY = PropertyName("array")
    }
}
