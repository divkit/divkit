package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PercentageSize

@Generated
internal class PercentageSizeSerializer : EntitySerializer<PercentageSize>(PercentageSize::class.java, "percentage") {
    override fun writeFields(value: PercentageSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
