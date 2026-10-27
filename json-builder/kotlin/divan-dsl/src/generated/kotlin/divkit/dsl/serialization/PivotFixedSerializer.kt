package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PivotFixed

@Generated
internal class PivotFixedSerializer : EntitySerializer<PivotFixed>(PivotFixed::class.java, "pivot-fixed") {
    override fun writeFields(value: PivotFixed, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(UNIT, properties.unit, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val UNIT = PropertyName("unit")
        val VALUE = PropertyName("value")
    }
}
