package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.OffsetDestination

@Generated
internal class OffsetDestinationSerializer : EntitySerializer<OffsetDestination>(OffsetDestination::class.java, "offset") {
    override fun writeFields(value: OffsetDestination, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
