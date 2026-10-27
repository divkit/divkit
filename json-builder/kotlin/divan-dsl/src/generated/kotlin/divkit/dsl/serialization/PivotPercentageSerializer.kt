package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PivotPercentage

@Generated
internal class PivotPercentageSerializer : EntitySerializer<PivotPercentage>(PivotPercentage::class.java, "pivot-percentage") {
    override fun writeFields(value: PivotPercentage, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
