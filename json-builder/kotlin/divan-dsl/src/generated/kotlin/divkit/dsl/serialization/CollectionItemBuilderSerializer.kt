package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.CollectionItemBuilder

@Generated
internal class CollectionItemBuilderSerializer : EntitySerializer<CollectionItemBuilder>(CollectionItemBuilder::class.java) {
    override fun writeFields(value: CollectionItemBuilder, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DATA, properties.data, provider)
        gen.writeProperty(DATA_ELEMENT_NAME, properties.dataElementName, provider)
        gen.writeProperty(PROTOTYPES, properties.prototypes, provider)
    }

    private companion object {
        val DATA = PropertyName("data")
        val DATA_ELEMENT_NAME = PropertyName("data_element_name")
        val PROTOTYPES = PropertyName("prototypes")
    }
}

@Generated
internal class CollectionItemBuilderPrototypeSerializer : EntitySerializer<CollectionItemBuilder.Prototype>(CollectionItemBuilder.Prototype::class.java) {
    override fun writeFields(value: CollectionItemBuilder.Prototype, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DIV, properties.div, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(SELECTOR, properties.selector, provider)
    }

    private companion object {
        val DIV = PropertyName("div")
        val ID = PropertyName("id")
        val SELECTOR = PropertyName("selector")
    }
}
