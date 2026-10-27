package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FixedSize

@Generated
internal class FixedSizeSerializer : EntitySerializer<FixedSize>(FixedSize::class.java, "fixed") {
    override fun writeFields(value: FixedSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
        gen.writeProperty(UNIT, properties.unit, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
        val UNIT = PropertyName("unit")
    }
}
