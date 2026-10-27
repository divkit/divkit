package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithPropertyWithDefaultValue

@Generated
internal class WithPropertyWithDefaultValueSerializer : EntitySerializer<WithPropertyWithDefaultValue>(WithPropertyWithDefaultValue::class.java, "entity_with_property_with_default_value") {
    override fun writeFields(value: WithPropertyWithDefaultValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR_AARRGGBB, properties.colorAarrggbb, provider)
        gen.writeProperty(COLOR_RRGGBB, properties.colorRrggbb, provider)
        gen.writeProperty(INT, properties.int, provider)
        gen.writeProperty(NESTED, properties.nested, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val COLOR_AARRGGBB = PropertyName("color_aarrggbb")
        val COLOR_RRGGBB = PropertyName("color_rrggbb")
        val INT = PropertyName("int")
        val NESTED = PropertyName("nested")
        val URL = PropertyName("url")
    }
}

@Generated
internal class WithPropertyWithDefaultValueNestedSerializer : EntitySerializer<WithPropertyWithDefaultValue.Nested>(WithPropertyWithDefaultValue.Nested::class.java) {
    override fun writeFields(value: WithPropertyWithDefaultValue.Nested, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(INT, properties.int, provider)
        gen.writeProperty(NON_OPTIONAL, properties.nonOptional, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val INT = PropertyName("int")
        val NON_OPTIONAL = PropertyName("non_optional")
        val URL = PropertyName("url")
    }
}
