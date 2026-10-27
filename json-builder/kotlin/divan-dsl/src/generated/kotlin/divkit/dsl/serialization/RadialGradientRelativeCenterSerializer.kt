package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.RadialGradientRelativeCenter

@Generated
internal class RadialGradientRelativeCenterSerializer : EntitySerializer<RadialGradientRelativeCenter>(RadialGradientRelativeCenter::class.java, "relative") {
    override fun writeFields(value: RadialGradientRelativeCenter, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
