package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithArrayOfNestedItems

@Generated
internal class WithArrayOfNestedItemsSerializer : EntitySerializer<WithArrayOfNestedItems>(WithArrayOfNestedItems::class.java, "entity_with_array_of_nested_items") {
    override fun writeFields(value: WithArrayOfNestedItems, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ITEMS = PropertyName("items")
    }
}

@Generated
internal class WithArrayOfNestedItemsItemSerializer : EntitySerializer<WithArrayOfNestedItems.Item>(WithArrayOfNestedItems.Item::class.java) {
    override fun writeFields(value: WithArrayOfNestedItems.Item, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ENTITY, properties.entity, provider)
        gen.writeProperty(PROPERTY, properties.property, provider)
    }

    private companion object {
        val ENTITY = PropertyName("entity")
        val PROPERTY = PropertyName("property")
    }
}
