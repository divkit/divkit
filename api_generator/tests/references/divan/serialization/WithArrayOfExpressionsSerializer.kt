package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithArrayOfExpressions

@Generated
internal class WithArrayOfExpressionsSerializer : EntitySerializer<WithArrayOfExpressions>(WithArrayOfExpressions::class.java, "entity_with_array_of_expressions") {
    override fun writeFields(value: WithArrayOfExpressions, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ITEMS = PropertyName("items")
    }
}
