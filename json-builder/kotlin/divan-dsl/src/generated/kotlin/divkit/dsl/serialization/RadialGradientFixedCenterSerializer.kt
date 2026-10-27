package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.RadialGradientFixedCenter

@Generated
internal class RadialGradientFixedCenterSerializer : EntitySerializer<RadialGradientFixedCenter>(RadialGradientFixedCenter::class.java, "fixed") {
    override fun writeFields(value: RadialGradientFixedCenter, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(UNIT, properties.unit, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val UNIT = PropertyName("unit")
        val VALUE = PropertyName("value")
    }
}
