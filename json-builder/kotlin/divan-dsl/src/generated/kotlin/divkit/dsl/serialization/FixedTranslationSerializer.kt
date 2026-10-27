package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FixedTranslation

@Generated
internal class FixedTranslationSerializer : EntitySerializer<FixedTranslation>(FixedTranslation::class.java, "translation-fixed") {
    override fun writeFields(value: FixedTranslation, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(UNIT, properties.unit, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val UNIT = PropertyName("unit")
        val VALUE = PropertyName("value")
    }
}
