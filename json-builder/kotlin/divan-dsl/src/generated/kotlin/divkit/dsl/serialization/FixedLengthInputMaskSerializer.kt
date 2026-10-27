package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FixedLengthInputMask

@Generated
internal class FixedLengthInputMaskSerializer : EntitySerializer<FixedLengthInputMask>(FixedLengthInputMask::class.java, "fixed_length") {
    override fun writeFields(value: FixedLengthInputMask, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALWAYS_VISIBLE, properties.alwaysVisible, provider)
        gen.writeProperty(PATTERN, properties.pattern, provider)
        gen.writeProperty(PATTERN_ELEMENTS, properties.patternElements, provider)
        gen.writeProperty(RAW_TEXT_VARIABLE, properties.rawTextVariable, provider)
    }

    private companion object {
        val ALWAYS_VISIBLE = PropertyName("always_visible")
        val PATTERN = PropertyName("pattern")
        val PATTERN_ELEMENTS = PropertyName("pattern_elements")
        val RAW_TEXT_VARIABLE = PropertyName("raw_text_variable")
    }
}

@Generated
internal class FixedLengthInputMaskPatternElementSerializer : EntitySerializer<FixedLengthInputMask.PatternElement>(FixedLengthInputMask.PatternElement::class.java) {
    override fun writeFields(value: FixedLengthInputMask.PatternElement, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(KEY, properties.key, provider)
        gen.writeProperty(PLACEHOLDER, properties.placeholder, provider)
        gen.writeProperty(REGEX, properties.regex, provider)
    }

    private companion object {
        val KEY = PropertyName("key")
        val PLACEHOLDER = PropertyName("placeholder")
        val REGEX = PropertyName("regex")
    }
}
