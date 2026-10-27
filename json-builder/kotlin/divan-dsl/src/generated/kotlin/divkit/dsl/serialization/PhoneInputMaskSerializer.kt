package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PhoneInputMask

@Generated
internal class PhoneInputMaskSerializer : EntitySerializer<PhoneInputMask>(PhoneInputMask::class.java, "phone") {
    override fun writeFields(value: PhoneInputMask, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(RAW_TEXT_VARIABLE, properties.rawTextVariable, provider)
    }

    private companion object {
        val RAW_TEXT_VARIABLE = PropertyName("raw_text_variable")
    }
}
