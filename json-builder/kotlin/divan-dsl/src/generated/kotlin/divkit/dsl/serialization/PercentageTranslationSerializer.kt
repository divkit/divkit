package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PercentageTranslation

@Generated
internal class PercentageTranslationSerializer : EntitySerializer<PercentageTranslation>(PercentageTranslation::class.java, "translation-percentage") {
    override fun writeFields(value: PercentageTranslation, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
    }
}
