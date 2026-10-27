package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.CurrencyInputMask

@Generated
internal class CurrencyInputMaskSerializer : EntitySerializer<CurrencyInputMask>(CurrencyInputMask::class.java, "currency") {
    override fun writeFields(value: CurrencyInputMask, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(LOCALE, properties.locale, provider)
        gen.writeProperty(RAW_TEXT_VARIABLE, properties.rawTextVariable, provider)
    }

    private companion object {
        val LOCALE = PropertyName("locale")
        val RAW_TEXT_VARIABLE = PropertyName("raw_text_variable")
    }
}
