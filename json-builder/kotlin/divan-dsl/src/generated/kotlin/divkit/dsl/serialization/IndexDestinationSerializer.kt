package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.IndexDestination

@Generated
internal class IndexDestinationSerializer : EntitySerializer<IndexDestination>(IndexDestination::class.java, "index") {
    override fun writeFields(value: IndexDestination, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
